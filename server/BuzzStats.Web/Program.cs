using BuzzStats.DTOs;
using BuzzStats.Kafka;
using BuzzStats.Logging;
using BuzzStats.WebApi.DTOs;
using Confluent.Kafka;
using Microsoft.AspNetCore;
using Microsoft.AspNetCore.Hosting;
using System;
using System.Threading.Tasks;

namespace BuzzStats.Web
{
    class Program
    {
        public static IWebHost BuildWebHost(string[] args) =>
            WebHost.CreateDefaultBuilder()
                .UseUrls("http://*:9000")
                .UseStartup<Startup>()
                .Build();

        static void Main(string[] args)
        {
            LogSetup.Setup();

            string brokerList = BrokerSelector.Select(args);
            var consumerOptions = ConsumerOptionsFactory.JsonValues<StoryEvent>(
                "BuzzStats.Web",
                "StoryChanged");
            var consumerApp = new ConsumerApp<Null, StoryEvent>(brokerList, consumerOptions)
            {
                HandleCancelKeyPress = false
            };

            consumerApp.MessageReceived += ConsumerApp_MessageReceived;

            Task task = Task.Run(() => consumerApp.Poll());

            Console.CancelKeyPress += (_, e) =>
            {
                e.Cancel = true; // prevent the process from terminating.
                consumerApp.IsCancelled = true;
            };

            BuildWebHost(args).Run();
            task.Wait();
        }

        private static void ConsumerApp_MessageReceived(object sender, Message<Null, StoryEvent> e)
        {
            Console.WriteLine($"Received story event {e.Value.EventType} for story {e.Value.StoryId}");
            var msg = e.Value;
            var repository = new MongoRepository();
            var recentActivity = new RecentActivity
            {
                StoryId = msg.StoryId,
                StoryUsername = msg.EventType == StoryEventType.StoryCreated ? msg.Username : null,
                StoryVoteUsername = msg.EventType == StoryEventType.StoryVoted ? msg.Username : null,
                CommentUsername = msg.EventType == StoryEventType.CommentCreated ? msg.Username : null
            };

            Task.Run(async () => await repository.Save(recentActivity))
                .GetAwaiter().GetResult();
        }
    }
}
