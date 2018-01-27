using BuzzStats.DTOs;
using BuzzStats.Kafka;
using BuzzStats.Parsing;
using NodaTime;
using System;
using System.Threading.Tasks;

namespace BuzzStats.StoryIngester
{
    public class Program
    {
        const string InputTopic = "StoryDiscovered";
        const string OutputTopic = "StoryParsed";

        public Program(IParserClient parserClient)
        {
            ParserClient = parserClient;
        }

        public IParserClient ParserClient { get; }

        public async Task<Story> Convert(string msg)
        {
            var storyId = System.Convert.ToInt32(msg);
            return await ParserClient.Story(storyId);
        }

        static void Main(string[] args)
        {
            Console.WriteLine("Starting Story Ingester");
            string brokerList = BrokerSelector.Select(args);

            var parserClient = new ParserClient(
                new UrlProvider("http://buzz.reality-tape.com/"),
                new Parser(SystemClock.Instance));

            var program = new Program(parserClient);

            var consumerOptions = ConsumerOptionsFactory.StringValues(
                "BuzzStats.StoryIngester",
                InputTopic);

            var producerOptions = ProducerOptionsFactory.JsonValues<Story>(OutputTopic);

            var streamingApp = new KeyLessOneToOneStreamingApp<string, Story>(
                brokerList,
                consumerOptions,
                producerOptions,
                program.Convert);
            streamingApp.Poll();
        }
    }
}
