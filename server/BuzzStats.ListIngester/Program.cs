using Autofac;
using BuzzStats.Kafka;
using BuzzStats.ListIngester.Mongo;
using BuzzStats.Parsing;
using Confluent.Kafka;
using Microsoft.Extensions.Logging;
using Microsoft.Extensions.Logging.Console;
using NodaTime;
using System;
using System.Linq;
using System.Reactive.Linq;
using System.Text;
using System.Threading;
using Yak.Configuration;
using Yak.Configuration.Autofac;
using Yak.Kafka;

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
                EnableAutoCommit = true,
                BrokerList = brokerList
            };
        }
    }

    public class ProducerBuilder
    {
        [ConfigurationValue]
        private string brokerList = "127.0.0.1";

        public ProducerObserver Build()
        {
            return new ProducerObserver(brokerList, "StoryExpired");
        }
    }

    public class Program
    {
        public Program(ConsumerObservable consumerObservable, IMessageConverter messageConverter, IRepository repository, ILogger logger, ProducerObserver producerObserver)
        {
            ConsumerObservable = consumerObservable ?? throw new ArgumentNullException(nameof(consumerObservable));
            MessageConverter = messageConverter ?? throw new ArgumentNullException(nameof(messageConverter));
            Repository = repository ?? throw new ArgumentNullException(nameof(repository));
            Logger = logger;
            ProducerObserver = producerObserver ?? throw new ArgumentNullException(nameof(producerObserver));
        }

        public ConsumerObservable ConsumerObservable { get; }
        private IMessageConverter MessageConverter { get; }
        private IRepository Repository { get; }
        public ILogger Logger { get; }
        public ProducerObserver ProducerObserver { get; }

        public void Start()
        {
            Logger.LogInformation("Starting List Ingester");

            var messagePublisher = new MessagePublisher(
                MessageConverter,
                Repository,
                Logger);

            ConsumerObservable.SubscribeConsumerEvents(new LogConsumerEvents<Ignore, byte[]>(Logger));
            ConsumerObservable
                .Select(Encoding.UTF8.GetString) // deserialize
                .Select(messagePublisher.HandleMessage) // process
                .SelectMany(x => x) // flatten list
                .Select(Encoding.UTF8.GetBytes) // serialize
                .Subscribe(ProducerObserver); // publish to producer

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

        private void Timer(MessagePublisher messagePublisher)
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
            builder.Register(c => c.Resolve<ProducerBuilder>().Build());

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