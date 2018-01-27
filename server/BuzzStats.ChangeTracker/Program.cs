using BuzzStats.Kafka;
using BuzzStats.DTOs;
using System;
using System.Threading.Tasks;
using BuzzStats.Logging;
using BuzzStats.Configuration;
using log4net;
using Confluent.Kafka;

namespace BuzzStats.ChangeTracker
{
    public class Program
    {
        const string InputTopic = "StoryParsed";
        const string OutputTopic = "StoryChanged";
        private static readonly ILog Log = LogManager.GetLogger(typeof(Program));
        private readonly IEventProducer eventProducer;
        private readonly IConsumerApp<Null, Story> consumer;
        private readonly ISerializingProducer<Null, StoryEvent> producer;

        public Program(IEventProducer eventProducer, IConsumerApp<Null, Story> consumer, ISerializingProducer<Null, StoryEvent> producer)
        {
            this.eventProducer = eventProducer;
            this.consumer = consumer;
            this.producer = producer;
        }

        static void Main(string[] args)
        {
            LogSetup.Setup();

            Console.WriteLine("Starting Change Tracker");
            ConfigurationBuilder.Build(args);
            string brokerList = ConfigurationBuilder.KafkaBroker;

            var consumerOptions = ConsumerOptionsFactory.JsonValues<Story>(
                "BuzzStats.ChangeTracker");
            var consumer = new ConsumerApp<Null, Story>(brokerList, consumerOptions);

            using (var producer = new ProducerBuilder<Null, StoryEvent>(brokerList, null, Serializers.Json<StoryEvent>()).Build())
            {
                var program = new Program(
                    new EventProducer(new MongoRepository(ConfigurationBuilder.MongoConnectionString)),
                    consumer,
                    producer);
                program.Poll();
            }
        }

        public void Poll()
        {
            consumer.MessageReceived += OnMessageReceived;
            consumer.Poll(InputTopic);
        }

        private void OnMessageReceived(object sender, Message<Null, Story> e)
        {
            Task.Run(async () =>
            {
                await OnMessageReceivedAsync(sender, e);
            }).GetAwaiter().GetResult();
        }

        private async Task OnMessageReceivedAsync(object sender, Message<Null, Story> e)
        {
            foreach (var storyEvent in await eventProducer.CreateEventsAsync(e.Value))
            {
                await producer.ProduceAsync(OutputTopic, null, storyEvent);
            }
        }
    }
}
