using Autofac;
using BuzzStats.DTOs;
using BuzzStats.Kafka;
using BuzzStats.Logging;
using BuzzStats.StoryUpdater.Mongo;
using Confluent.Kafka;
using Confluent.Kafka.Serialization;
using log4net;
using System;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using Yak.Configuration;
using Yak.Configuration.Autofac;
using Yak.Kafka;

namespace BuzzStats.StoryUpdater
{
    class ConsumerBuilder
    {
        [ConfigurationValue]
        private string brokerList = "127.0.0.1";

        public IConsumerApp<Null, StoryEvent> Build()
        {
            return new ConsumerApp<Null, StoryEvent>(
                brokerList,
                typeof(ConsumerBuilder).Namespace,
                null,
                new JsonDeserializer<StoryEvent>());
        }
    }

    class ProducerBuilder
    {
        [ConfigurationValue]
        private string brokerList = "127.0.0.1";

        public ISerializingProducer<Null, string> Build()
        {
            return new ProducerBuilder<Null, string>(brokerList, null,
                new StringSerializer(Encoding.UTF8)).Build();
        }
    }

    class Program
    {
        const string InputTopic = "StoryChanged";
        const string OutputTopic = "StoryExpired";

        private static readonly ILog Log = LogManager.GetLogger(typeof(Program));

        private readonly IConsumerApp<Null, StoryEvent> _consumer;
        private readonly ISerializingProducer<Null, string> _producer;
        private readonly IRepository _repository;

        public Program(
            IConsumerApp<Null, StoryEvent> consumer,
            ISerializingProducer<Null, string> producer,
            IRepository repository)
        {
            _consumer = consumer;
            _producer = producer;
            _repository = repository;
        }

        public void Poll()
        {
            _consumer.MessageReceived += (_, msg) =>
            {
                Log.InfoFormat("Registering recent activity for story {0}", msg.Value.StoryId);
                Task.Run(async () => await _repository.RegisterChangeEvent(msg.Value))
                    .GetAwaiter().GetResult();
            };

            var oldestStoryUpdater = new OldestStoryUpdater(
                _repository,
                _producer,
                OutputTopic);

            using (var timer = new Timer(
                _ => oldestStoryUpdater.Update(),
                null,
                TimeSpan.FromSeconds(10),
                TimeSpan.FromSeconds(10)))
            {
                _consumer.Poll(InputTopic);
            }
        }

        static void Main(string[] args)
        {
            LogSetup.Setup();
            Console.WriteLine("Starting Story Updater");

            var builder = new ContainerBuilder();
            builder.RegisterType<Program>()
                .InjectConfiguration();

            builder.RegisterType<Repository>()
                .As<IRepository>()
                .InjectConfiguration();

            builder.RegisterType<ConsumerBuilder>()
                .InjectConfiguration();

            builder.RegisterType<ProducerBuilder>()
                .InjectConfiguration();

            builder.Register(c => c.Resolve<ConsumerBuilder>().Build());

            builder.Register(c => c.Resolve<ProducerBuilder>().Build());

            var container = builder.Build();
            using (var scope = container.BeginLifetimeScope())
            {
                var program = scope.Resolve<Program>();
                program.Poll();
            }
        }
    }
}
