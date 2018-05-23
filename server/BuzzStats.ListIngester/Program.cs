using Autofac;
using BuzzStats.Kafka;
using BuzzStats.ListIngester.Mongo;
using BuzzStats.Parsing;
using BuzzStats.Parsing.DTOs;
using Confluent.Kafka;
using Microsoft.Extensions.Logging;
using NodaTime;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Reactive.Linq;
using System.Reactive.Threading.Tasks;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using Yak.Configuration;
using Yak.Configuration.Autofac;

namespace BuzzStats.ListIngester
{
    public class ConsumerOptionsBuilder
    {
        [ConfigurationValue]
        private string brokerList = "127.0.0.1";

        public ConsumerOptions Build()
        {
            return new ConsumerOptions
            {
                ConsumerId = "BuzzStats.ListIngester",
                Topic = "ListExpired",
                BrokerList = brokerList
            };
        }
    }

    public class ProducerBuilder
    {
        [ConfigurationValue]
        private string brokerList = "127.0.0.1";

        public ProducerObserver Build(ILogger logger)
        {
            return new ProducerObserver(brokerList, "StoryExpired", logger);
        }
    }

    public class Program
    {
        public Program(ConsumerObservable consumerObservable, IMessageConverter messageConverter, IRepository repository, ILogger logger, ProducerObserver producerObserver, IParserClient parserClient)
        {
            ConsumerObservable = consumerObservable ?? throw new ArgumentNullException(nameof(consumerObservable));
            MessageConverter = messageConverter ?? throw new ArgumentNullException(nameof(messageConverter));
            Repository = repository ?? throw new ArgumentNullException(nameof(repository));
            Logger = logger;
            ProducerObserver = producerObserver ?? throw new ArgumentNullException(nameof(producerObserver));
            ParserClient = parserClient;
        }

        public ConsumerObservable ConsumerObservable { get; }
        private IMessageConverter MessageConverter { get; }
        private IRepository Repository { get; }
        public ILogger Logger { get; }
        public ProducerObserver ProducerObserver { get; }
        public IParserClient ParserClient { get; }

        private IObservable<StoryListingSummaries> Parse(Message<Ignore, byte[]> message)
        {
            return Observable.Return(message)
                .Select(m => m.Value)
                .Select(Encoding.UTF8.GetString) // deserialize
                .Select(MessageConverter.Parse) // parse message
                .Select(t => ParserClient.ListingAsync(t.Item1, t.Item2).ToObservable())
                .Merge(); // unwrap observable
        }

        private async Task<Message<Null, byte[]>> PublishAsync(StoryListingSummary storyListingSummary)
        {
            bool added = await Repository.AddIfMissing(storyListingSummary.StoryId);
            if (added)
            {
                return await ProducerObserver.ProduceAsync(Encoding.UTF8.GetBytes(storyListingSummary.StoryId.ToString()));
            }

            return null;
        }

        private async Task<ICollection<Message<Null, byte[]>>> CombineAsync(StoryListingSummaries storyListingSummaries)
        {
            List<Message<Null, byte[]>> result = new List<Message<Null, byte[]>>();
            foreach (var s in storyListingSummaries)
            {
                result.Add(await PublishAsync(s));
            }

            return result;
        }


        public void Start()
        {
            Logger.LogInformation("Starting List Ingester");

            ConsumerObservable.SubscribeConsumerEvents(new LogConsumerEvents<Ignore, byte[]>(Logger));

            var q = ConsumerObservable
                .Do(_ => Logger.LogInformation("Received message {0}", _.Offset))
                .PackPayload(Parse)
                .Do(_ => Logger.LogInformation("Parsing complete {0}", _.Item1.Offset))
                .RepackPayloadTask(CombineAsync)
                .Do(_ => Logger.LogInformation("Published {0} messages to producer for original message {1}", _.Item2.Count, _.Item1.Offset))
                .RepackPayloadTask(ConsumerObservable.Commit);

            q.Subscribe(_ =>
            {
                Logger.LogInformation("Message {0} processed", _.Item1.Offset);
            });


            using (CancellationTokenSource cts = new CancellationTokenSource())
            {
                CancellationToken token = cts.Token;

                Console.CancelKeyPress += (_, e) =>
                {
                    Logger.LogInformation("Shutting down");
                    e.Cancel = true; // prevent the process from terminating.
                    cts.Cancel();
                };

                Console.WriteLine("Press Ctrl-C to exit");

                //Timer(messagePublisher);

                Logger.LogInformation("Polling starting");
                ConsumerObservable.Poll(token);
                Logger.LogInformation("Polling finished");
            }

            Logger.LogInformation("Exiting app");
        }

        private void Timer()
        {
            // TODO command line argument and/or environment variable for number of pages to go through
            const int pageNumber = 4;
            var inputMessages = PageBuilder.Build(pageNumber).ToArray();
            var q = from tick in Observable.Timer(dueTime: TimeSpan.FromSeconds(5), period: TimeSpan.FromMinutes(1))
                    select inputMessages[tick % inputMessages.Length];

            //q.Subscribe(messagePublisher.HandleMessage);
        }

        static void Main(string[] args)
        {
            var builder = new ContainerBuilder();
            builder.RegisterType<Program>();

            // consumer
            builder.RegisterType<ConsumerOptionsBuilder>().InjectConfiguration();
            builder.Register(c => c.Resolve<ConsumerOptionsBuilder>().Build()); // ConsumerOptions

            builder.RegisterType<ConsumerObservable>();

            // producer
            builder.RegisterType<ProducerBuilder>().InjectConfiguration();
            builder.Register(c => c.Resolve<ProducerBuilder>().Build(c.Resolve<ILogger>()));

            // parser etc
            builder.RegisterType<ParserClient>().As<IParserClient>();
            builder.RegisterType<MessageConverter>().As<IMessageConverter>();
            builder.RegisterType<Parser>().As<IParser>();
            builder.Register(c => new UrlProvider("http://buzz.reality-tape.com/")).As<IUrlProvider>();
            builder.Register(c => SystemClock.Instance).As<IClock>();

            // repository
            builder.RegisterType<Repository>().As<IRepository>().InjectConfiguration();

            // logging
            builder.RegisterType<LoggerFactory>()
                .As<ILoggerFactory>()
                .OnActivating(c => c.Instance.AddConsole());

            builder.Register(c => c.Resolve<ILoggerFactory>().CreateLogger(typeof(Program))).As<ILogger>();

            var container = builder.Build();
            using (var scope = container.BeginLifetimeScope())
            {
                var program = scope.Resolve<Program>();
                program.Start();
            }
        }
    }

    //class Processor
    //{
    //    // TODO: should be as simple as this method
    //    public async Task<IEnumerable<int>> ListExpired(Tuple<StoryListing, int> storyListingPage)
    //    {
    //        return null;
    //    }
    //}
}


//TODO use configuration
//                var loggingConfiguration = new ConfigurationBuilder()
//                .SetBasePath(Directory.GetCurrentDirectory())
//                .AddJsonFile("logging.json", optional: false, reloadOnChange: true)
//.Build();
