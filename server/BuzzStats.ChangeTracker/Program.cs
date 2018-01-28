using BuzzStats.Kafka;
using BuzzStats.DTOs;
using System;
using System.Threading.Tasks;
using BuzzStats.Logging;
using BuzzStats.Configuration;
using Confluent.Kafka;
using BuzzStats.ChangeTracker.Mongo;

namespace BuzzStats.ChangeTracker
{
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

        static void Main(string[] args)
        {
            LogSetup.Setup();

            Console.WriteLine("Starting Change Tracker");
            ConfigurationBuilder.Build(args);
            string brokerList = ConfigurationBuilder.KafkaBroker;
            string consumerId = typeof(Program).Namespace;
            StreamingAppBuilder.JsonToJson<Story, StoryEvent>()
                .WithBrokerList(brokerList)
                .WithConsumerId(consumerId)
                .Run((consumer, producer) =>
            {
                var program = new Program(
                    new ChangeDetector(new Repository(ConfigurationBuilder.MongoConnectionString)),
                    consumer,
                    producer);
                program.Poll();
            });
        }

        public void Poll()
        {
            _consumer.MessageReceived += OnMessageReceived;
            _consumer.Poll(InputTopic);
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
