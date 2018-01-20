using BuzzStats.Parsing;
using BuzzStats.Parsing.DTOs;
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
    class Program
    {
        const string InputTopic = "ListExpired";
        const string OutputTopic = "StoryDiscovered";

        async static Task<StoryListingSummary[]> Parse()
        {
            var parserClient = new ParserClient(
                new UrlProvider("http://buzz.reality-tape.com/"),
                new Parser(SystemClock.Instance));

            return (await parserClient.Listing(StoryListing.Home, page: 0)).ToArray();
        }

        async static Task<Message<Null, string>> Post(string brokerList, StoryListingSummary storyListingSummary)
        {
            var config = new Dictionary<string, object>
            {
                { "bootstrap.servers", brokerList }
            };

            using (var producer = new Producer<Null, string>(
                config,
                null,
                new StringSerializer(Encoding.UTF8)
                ))
            {
                return await producer.ProduceAsync(
                    OutputTopic,
                    null,
                    "Story " + storyListingSummary.StoryId + " found");
            }
        }

        async static Task AsyncMain(string[] args)
        {
            string brokerList = args[0];

            var config = new Dictionary<string, object>
            {
                { "group.id", "simple-csharp-consumer" },
                { "bootstrap.servers", brokerList }
            };

            using (var consumer = new Consumer<Ignore, string>(config, null, new StringDeserializer(Encoding.UTF8)))
            {
                consumer.Assign(new List<TopicPartitionOffset> { new TopicPartitionOffset(InputTopic, 0, 0) });

                // Raised on critical errors, e.g. connection failures or all brokers down.
                consumer.OnError += (_, error)
                    => Console.WriteLine($"Error: {error}");

                // Raised on deserialization errors or when a consumed message has an error != NoError.
                consumer.OnConsumeError += (_, error)
                    => Console.WriteLine($"Consume error: {error}");

                while (true)
                {
                    Message<Ignore, string> msg;
                    if (consumer.Consume(out msg, TimeSpan.FromSeconds(1)))
                    {
                        Console.WriteLine($"Topic: {msg.Topic} Partition: {msg.Partition} Offset: {msg.Offset} {msg.Value}");
                        var storyListingSummaries = await Parse();
                        foreach (var storyListingSummary in storyListingSummaries)
                        {
                            await Post(brokerList, storyListingSummary);
                        }
                    }
                }
            }

        }

        static void Main(string[] args)
        {
            Console.WriteLine("Hello World!");
            Task.Run(async () =>
            {
                await AsyncMain(args);
            }).GetAwaiter().GetResult();
        }
    }
}
