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

        static void Main(string[] args)
        {
            LogSetup.Setup();
            Console.WriteLine("Starting Story Ingester");
            ConfigurationBuilder.Build(args);
            string brokerList = ConfigurationBuilder.KafkaBroker;
            string consumerId = typeof(Program).Namespace;
            var parserClient = new ParserClient(
                new UrlProvider("http://buzz.reality-tape.com/"),
                new Parser(SystemClock.Instance));

            StreamingAppBuilder.StringToJson<Story>()
                .WithBrokerList(brokerList)
                .WithConsumerId(consumerId)
                .Run((consumer, producer) =>
                {
                    var program = new Program(parserClient, consumer, producer);
                    program.Poll();
                });
        }

        public void Poll()
        {
            _consumer.MessageReceived += OnMessageReceived;
            _consumer.Poll(InputTopic);
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
            var story = await _parserClient.Story(storyId);
            await _producer.ProduceAsync(OutputTopic, null, story);
        }
    }
}
