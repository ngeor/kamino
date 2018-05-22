using BuzzStats.Kafka;
using BuzzStats.DTOs;
using System;
using System.Threading.Tasks;
using BuzzStats.Logging;
using Confluent.Kafka;
using BuzzStats.ChangeTracker.Mongo;
using Autofac;
using Yak.Configuration.Autofac;
using Yak.Configuration;
using Yak.Kafka;

namespace BuzzStats.ChangeTracker
{
    class ConsumerBuilder
    {
        [ConfigurationValue]
        private string brokerList = "127.0.0.1";

        public IConsumerApp<Null, Story> Build()
        {
            return new ConsumerApp<Null, Story>(
                brokerList,
                typeof(ConsumerBuilder).Namespace,
                null,
                new JsonDeserializer<Story>());
        }
    }

    class ProducerBuilder
    {
        [ConfigurationValue]
        private string brokerList = "127.0.0.1";

        public ISerializingProducer<Null, StoryEvent> Build()
        {
            return new ProducerBuilder<Null, StoryEvent>(brokerList, null, 
                new JsonSerializer<StoryEvent>()).Build();
        }
    }

    public class Program
    {
        const string InputTopic = "StoryParsed";
        const string OutputTopic = "StoryChanged";
        private readonly IChangeDetector _changeDetector;
        private readonly IConsumerApp<Null, Story> _consumer;
        private readonly ISerializingProducer<Null, StoryEvent> _producer;

        public Program(IChangeDetector changeDetector, IConsumerApp<Null, Story> consumer, ISerializingProducer<Null, StoryEvent> producer)
        {
            _changeDetector = changeDetector;
            _consumer = consumer;
            _producer = producer;
        }

        public void Poll()
        {
            // TODO async handler here instead of event delegate
            _consumer.MessageReceived += OnMessageReceived;
            _consumer.Poll(InputTopic);
        }

        static void Main(string[] args)
        {
            LogSetup.Setup();
            Console.WriteLine("Starting Change Tracker");

            var builder = new ContainerBuilder();
            builder.RegisterType<Program>()
                .InjectConfiguration();

            builder.RegisterType<Repository>()
                .As<IRepository>()
                .InjectConfiguration();

            builder.RegisterType<ChangeDetector>().AsImplementedInterfaces();

            builder.RegisterType<ConsumerBuilder>()
                .InjectConfiguration();

            builder.RegisterType<ProducerBuilder>()
                .InjectConfiguration();

            builder.Register(c => c.Resolve<ConsumerBuilder>().Build()).As<IConsumerApp<Null, Story>>();

            builder.Register(c => c.Resolve<ProducerBuilder>().Build()).As<ISerializingProducer<Null, StoryEvent>>();

            var container = builder.Build();
            using (var scope = container.BeginLifetimeScope())
            {
                var program = scope.Resolve<Program>();
                program.Poll();
            }
        }

        private void OnMessageReceived(object sender, Message<Null, Story> e)
        {
            Task.Run(async () =>
            {
                await OnMessageReceivedAsync(e);
            }).GetAwaiter().GetResult();
        }

        private async Task OnMessageReceivedAsync(Message<Null, Story> e)
        {
            foreach (var storyEvent in await _changeDetector.FindChangesAsync(e.Value))
            {
                await _producer.ProduceAsync(OutputTopic, null, storyEvent);
            }
        }
    }
}
