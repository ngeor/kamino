using BuzzStats.Kafka;
using BuzzStats.DTOs;
using System;
using System.Threading.Tasks;
using BuzzStats.Logging;
using BuzzStats.Configuration;
using Confluent.Kafka;

namespace BuzzStats.ChangeTracker
{
    public class Program
    {
        const string InputTopic = "StoryParsed";
        const string OutputTopic = "StoryChanged";
        private readonly IEventProducer _eventProducer;
        private readonly IConsumerApp<Null, Story> _consumer;
        private readonly ISerializingProducer<Null, StoryEvent> _producer;

        public Program(IEventProducer eventProducer, IConsumerApp<Null, Story> consumer, ISerializingProducer<Null, StoryEvent> producer)
        {
            _eventProducer = eventProducer;
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
                    new EventProducer(new MongoRepository(ConfigurationBuilder.MongoConnectionString)),
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
            foreach (var storyEvent in await _eventProducer.CreateEventsAsync(e.Value))
            {
                await _producer.ProduceAsync(OutputTopic, null, storyEvent);
            }
        }
    }
}
