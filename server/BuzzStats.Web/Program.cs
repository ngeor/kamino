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
        private static IWebHost BuildWebHost(string[] args) =>
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
            var repository = new MongoRepository();
            var app = new Program(repository);
            consumerApp.MessageReceived += app.OnMessageReceived;

            Task task = Task.Run(() => consumerApp.Poll());

            Console.CancelKeyPress += (_, e) =>
            {
                e.Cancel = true; // prevent the process from terminating.
                consumerApp.IsCancelled = true;
            };

            BuildWebHost(args).Run();
            task.Wait();
        }

        public Program(IRepository repository)
        {
            Repository = repository ?? throw new ArgumentNullException(nameof(repository));
        }

        private IRepository Repository { get; }

        public void OnMessageReceived(object sender, Message<Null, StoryEvent> e)
        {
            Console.WriteLine($"Received story event {e.Value.EventType} for story {e.Value.StoryId}");
            var msg = e.Value;
            
            var recentActivity = new RecentActivity
            {
                StoryId = msg.StoryId,
                StoryUsername = msg.EventType == StoryEventType.StoryCreated ? msg.Username : null,
                StoryVoteUsername = msg.EventType == StoryEventType.StoryVoted ? msg.Username : null,
                CommentUsername = msg.EventType == StoryEventType.CommentCreated ? msg.Username : null
            };

            Task.Run(async () => await Repository.Save(recentActivity))
                .GetAwaiter().GetResult();
        }
    }
}
