using BuzzStats.Configuration;
using BuzzStats.DTOs;
using BuzzStats.Kafka;
using BuzzStats.Logging;
using Confluent.Kafka;
using log4net;
using System;
using System.Threading;
using System.Threading.Tasks;

namespace BuzzStats.StoryUpdater
{
    class Program
    {
        const string InputTopic = "StoryChanged";
        const string OutputTopic = "StoryExpired";

        private static readonly ILog Log = LogManager.GetLogger(typeof(Program));

        protected Program()
        {
        }

        static void Main(string[] args)
        {
            LogSetup.Setup();
            Console.WriteLine("Starting Story Updater");
            ConfigurationBuilder.Build(args);
            string brokerList = ConfigurationBuilder.KafkaBroker;

            var consumerOptions = ConsumerOptionsFactory.JsonValues<StoryEvent>(
                "BuzzStats.StoryUpdater");
            var consumer = new ConsumerApp<Null, StoryEvent>(brokerList, consumerOptions);
            var repository = new MongoRepository(ConfigurationBuilder.MongoConnectionString);

            using (var producer = new ProducerBuilder<Null, string>(brokerList, null, Serializers.String()).Build())
            {
                consumer.MessageReceived += (_, msg) =>
                {
                    Log.InfoFormat("Registering recent activity for story {0}", msg.Value.StoryId);
                    Task.Run(async () => await repository.RegisterChangeEvent(msg.Value))
                        .GetAwaiter().GetResult();
                };

                var oldestStoryUpdater = new OldestStoryUpdater(
                    repository,
                    producer,
                    OutputTopic);

                using (var timer = new Timer(
                    _ => oldestStoryUpdater.Update(),
                    null,
                    TimeSpan.FromSeconds(10),
                    TimeSpan.FromSeconds(10)))
                {
                    consumer.Poll(InputTopic);
                }
            }
        }
    }
}
