using Autofac;
using BuzzStats.DTOs;
using BuzzStats.Kafka;
using BuzzStats.Parsing;
using Confluent.Kafka;
using Confluent.Kafka.Serialization;
using Microsoft.Extensions.Logging;
using NodaTime;
using System;
using System.Text;
using System.Threading.Tasks;
using Yak.Configuration;
using Yak.Configuration.Autofac;
using Yak.Kafka;

namespace BuzzStats.StoryIngester
{
    class ConsumerBuilder
    {
        [ConfigurationValue]
        private string brokerList = "127.0.0.1";

        public IConsumerApp<Null, string> Build()
        {
            return new ConsumerApp<Null, string>(
                brokerList,
                typeof(ConsumerBuilder).Namespace,
                null,
                new StringDeserializer(Encoding.UTF8));
        }
    }

    class ProducerBuilder
    {
        [ConfigurationValue]
        private string brokerList = "127.0.0.1";

        public ISerializingProducer<Null, Story> Build()
        {
            return new ProducerBuilder<Null, Story>(brokerList, null,
                new JsonSerializer<Story>()).Build();
        }
    }

    public class Program
    {
        const string InputTopic = "StoryExpired";
        const string OutputTopic = "StoryParsed";
        private readonly IParserClient _parserClient;
        private readonly IConsumerApp<Null, string> _consumer;
        private readonly ISerializingProducer<Null, Story> _producer;

        public Program(
            IParserClient parserClient,
            IConsumerApp<Null, string> consumer,
            ISerializingProducer<Null, Story> producer)
        {
            _parserClient = parserClient;
            _consumer = consumer;
            _producer = producer;
        }

        public void Poll()
        {
            _consumer.MessageReceived += OnMessageReceived;
            _consumer.Poll(InputTopic);
        }

        static void Main(string[] args)
        {
            Console.WriteLine("Starting Story Ingester");

            var builder = new ContainerBuilder();
            builder.RegisterType<Program>()
                .InjectConfiguration();

            builder.RegisterType<ConsumerBuilder>()
                .InjectConfiguration();

            builder.RegisterType<ProducerBuilder>()
                .InjectConfiguration();

            builder.Register(c => c.Resolve<ConsumerBuilder>().Build());

            builder.Register(c => c.Resolve<ProducerBuilder>().Build());

            builder.RegisterType<ParserClient>().As<IParserClient>();
            builder.RegisterType<Parser>().As<IParser>();
            builder.Register(c => new UrlProvider("http://buzz.reality-tape.com/")).As<IUrlProvider>();
            builder.Register(c => SystemClock.Instance).As<IClock>();

            builder.RegisterType<LoggerFactory>()
                .As<ILoggerFactory>()
                .OnActivating(c => c.Instance.AddConsole());

            builder.Register(c => c.Resolve<ILoggerFactory>().CreateLogger(typeof(Program))).As<ILogger>();

            var container = builder.Build();
            using (var scope = container.BeginLifetimeScope())
            {
                var program = scope.Resolve<Program>();
                program.Poll();
            }
        }

        private void OnMessageReceived(object sender, Message<Null, string> e)
        {
            Task.Run(async () =>
            {
                await OnMessageReceivedAsync(e);
            }).GetAwaiter().GetResult();
        }

        private async Task OnMessageReceivedAsync(Message<Null, string> e)
        {
            var storyId = Convert.ToInt32(e.Value);
            var story = await _parserClient.StoryAsync(storyId);
            await _producer.ProduceAsync(OutputTopic, null, story);
        }
    }
}
