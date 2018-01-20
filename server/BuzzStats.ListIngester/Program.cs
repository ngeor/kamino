using BuzzStats.Kafka;
using BuzzStats.Parsing;
using BuzzStats.Parsing.DTOs;
using Confluent.Kafka;
using Confluent.Kafka.Serialization;
using NodaTime;
using System;
using System.Collections.Generic;
using System.Diagnostics;
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
            var result = listings.Select(listing => $"Found story {listing.StoryId}")
                .ToArray();
            return result;
        }

        static void Main(string[] args)
        {
            Console.WriteLine("Hello World!");
            string brokerList = args[0];

            var parserClient = new ParserClient(
                new UrlProvider("http://buzz.reality-tape.com/"),
                new Parser(SystemClock.Instance));

            var program = new Program(parserClient);

            var streamingApp = new StreamingApp(
                brokerList,
                "BuzzStats.ListIngester",
                InputTopic,
                OutputTopic,
                program.Convert);
            streamingApp.Poll();
        }
    }
}
