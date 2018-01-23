using BuzzStats.Kafka;
using BuzzStats.Parsing;
using Confluent.Kafka;
using Confluent.Kafka.Serialization;
using NodaTime;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BuzzStats.ListIngester
{
    public class Program
    {
        const string InputTopic = "ListExpired";
        const string OutputTopic = "StoryDiscovered";

        public Program(IParserClient parserClient)
        {
            ParserClient = parserClient;
        }

        public IParserClient ParserClient { get; }

        public async Task<IEnumerable<string>> Convert(string msg)
        {
            var listings = await ParserClient.Listing(StoryListing.Home, 0);
            var result = listings
                .Select(listing => $"{listing.StoryId}")
                .ToArray();
            return result;
        }

        static void Main(string[] args)
        {
            Console.WriteLine("Starting List Ingester");
            string brokerList = BrokerSelector.Select(args);

            var parserClient = new ParserClient(
                new UrlProvider("http://buzz.reality-tape.com/"),
                new Parser(SystemClock.Instance));

            var program = new Program(parserClient);

            var consumerOptions = ConsumerOptionsFactory.StringValues(
                "BuzzStats.ListIngester",
                InputTopic);

            var producerOptions = ProducerOptionsFactory.StringValues(OutputTopic);

            var streamingApp = new KeyLessOneToManyStreamingApp<string, string>(
                brokerList,
                consumerOptions,
                producerOptions,
                program.Convert);
            streamingApp.Poll();
        }
    }
}
