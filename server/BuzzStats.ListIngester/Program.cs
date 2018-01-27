using BuzzStats.Kafka;
using BuzzStats.Logging;
using BuzzStats.Parsing;
using Confluent.Kafka;
using log4net;
using NodaTime;
using System;
using System.Linq;

namespace BuzzStats.ListIngester
{
    public class Program
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof(Program));

        const string InputTopic = "ListExpired";
        const string OutputTopic = "StoryDiscovered";

        static void Main(string[] args)
        {
            LogSetup.Setup();
            Console.WriteLine("Starting List Ingester");
            string brokerList = BrokerSelector.Select(args);

            var consumerOptions = ConsumerOptionsFactory.StringValues(
                "BuzzStats.ListIngester",
                InputTopic);
            var consumer = new ConsumerApp<Null, string>(brokerList, consumerOptions);

            using (var producer = new ProducerBuilder<Null, string>(brokerList, null, Serializers.String()).Build())
            { 
                var parserClient = new ParserClient(
                    new UrlProvider("http://buzz.reality-tape.com/"),
                    new Parser(SystemClock.Instance));

                var messageConverter = new MessageConverter(parserClient);
                var messagePublisher = new MessagePublisher(messageConverter, producer, OutputTopic);
            
                consumer.MessageReceived += (_, msg) =>
                {
                    messagePublisher.HandleMessage(msg.Value);
                };

                // TODO command line argument and/or environment variable for number of pages to go through
                const int pageNumber = 3;
                using (Cron cron = new Cron(
                    messagePublisher,
                    TimeSpan.FromSeconds(5),
                    TimeSpan.FromMinutes(1),
                    PageBuilder.Build(pageNumber).ToArray()))
                {
                    consumer.Poll();
                }
            }
        }
    }
}
