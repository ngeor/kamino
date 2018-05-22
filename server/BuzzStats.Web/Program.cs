using Autofac;
using BuzzStats.DTOs;
using BuzzStats.Kafka;
using BuzzStats.Logging;
using BuzzStats.Web.Mongo;
using Confluent.Kafka;
using Microsoft.AspNetCore;
using Microsoft.AspNetCore.Hosting;
using System;
using System.Threading.Tasks;
using Yak.Configuration;
using Yak.Configuration.Autofac;
using Yak.Kafka;
using Autofac.Extensions.DependencyInjection;

namespace BuzzStats.Web
{
    class ConsumerBuilder
    {
        [ConfigurationValue]
        private string brokerList = "127.0.0.1";

        public IConsumerApp<Null, StoryEvent> Build()
        {
            return new ConsumerApp<Null, StoryEvent>(
                brokerList,
                typeof(ConsumerBuilder).Namespace,
                null,
                new JsonDeserializer<StoryEvent>())
            {
                HandleCancelKeyPress = false
            };
        }
    }

    class Program
    {
        private readonly IConsumerApp<Null, StoryEvent> consumerApp;

        private static IWebHost BuildWebHost(string[] args) =>
            WebHost.CreateDefaultBuilder()
                .ConfigureServices(services => services.AddAutofac())
                .UseUrls("http://*:9000")
                .UseStartup<Startup>()
                .Build();

        static void Main(string[] args)
        {
            LogSetup.Setup();
            Console.WriteLine("Starting Web");
            Task webTask = BuildWebHost(args).RunAsync();

            Program program = Program.Instance;
            Task pollTask = Task.Run(() => program.Poll());

            Console.CancelKeyPress += (_, e) =>
            {
                program.CancelPoll();
                e.Cancel = true;
            };

            webTask.Wait();
            pollTask.Wait();
        }

        public Program(IRepository repository, IConsumerApp<Null, StoryEvent> consumerApp)
        {
            Repository = repository ?? throw new ArgumentNullException(nameof(repository));
            this.consumerApp = consumerApp;
            consumerApp.MessageReceived += OnMessageReceived;
        }

        private IRepository Repository { get; }
        public static Program Instance { get; internal set; }

        public void Poll()
        {
            consumerApp.Poll("StoryChanged");
        }

        public void CancelPoll()
        {
            consumerApp.IsCancelled = true;
        }

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
