using BuzzStats.Configuration;
using BuzzStats.DTOs;
using BuzzStats.Kafka;
using BuzzStats.Logging;
using BuzzStats.Parsing;
using Confluent.Kafka;
using NodaTime;
using System;
using System.Threading.Tasks;

namespace BuzzStats.StoryIngester
{
    public class Program
    {
        const string InputTopic = "StoryExpired";
        const string OutputTopic = "StoryParsed";
        private readonly IParserClient parserClient;
        private readonly IConsumerApp<Null, string> consumer;
        private readonly ISerializingProducer<Null, Story> producer;

        public Program(
            IParserClient parserClient,
            IConsumerApp<Null, string> consumer,
            ISerializingProducer<Null, Story> producer)
        {
            this.parserClient = parserClient;
            this.consumer = consumer;
            this.producer = producer;
        }

        static void Main(string[] args)
        {
            LogSetup.Setup();
            Console.WriteLine("Starting Story Ingester");
            ConfigurationBuilder.Build(args);
            string brokerList = ConfigurationBuilder.KafkaBroker;

            var parserClient = new ParserClient(
                new UrlProvider("http://buzz.reality-tape.com/"),
                new Parser(SystemClock.Instance));

            var consumerOptions = ConsumerOptionsFactory.StringValues(
                "BuzzStats.StoryIngester");
            var consumer = new ConsumerApp<Null, string>(brokerList, consumerOptions);

            var producerOptions = ProducerOptionsFactory.JsonValues<Story>(OutputTopic);
            using (var producer = new ProducerBuilder<Null, Story>(brokerList, null, Serializers.Json<Story>()).Build())
            {
                var program = new Program(parserClient, consumer, producer);
                program.Poll();
            }
        }

        public void Poll()
        {
            consumer.MessageReceived += OnMessageReceived;
            consumer.Poll(InputTopic);
        }

        private void OnMessageReceived(object sender, Message<Null, string> e)
        {
            Task.Run(async () =>
            {
                await OnMessageReceivedAsync(sender, e);
            }).GetAwaiter().GetResult();
        }

        private async Task OnMessageReceivedAsync(object sender, Message<Null, string> e)
        {
            var storyId = Convert.ToInt32(e.Value);
            var story = await parserClient.Story(storyId);
            await producer.ProduceAsync(OutputTopic, null, story);
        }
    }
}
