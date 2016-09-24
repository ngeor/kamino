// --------------------------------------------------------------------------------
// <copyright file="CrawlApp.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2014
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/09/04
// * Time: 1:27 μμ
// --------------------------------------------------------------------------------

using System;
using System.Linq;
using System.Reflection;
using System.Threading;
using log4net;
using Microsoft.Practices.ServiceLocation;
using Newtonsoft.Json;
using NGSoftware.Common;
using NGSoftware.Common.Messaging;
using NGSoftware.Common.WebServices;
using BuzzStats.Configuration;
using BuzzStats.Data;
using BuzzStats.Persister;
using BuzzStats.Services;
using BuzzStats.Tasks;

namespace BuzzStats.Crawl
{
    [CommandLine("-nohost -skipIngesters -skipPollers -useBackgroundThreads -skipCrawlerEventsForwarder",
        "Starts crawling. Add -nohost to avoid hosting the remote control service.")]
    public class CrawlApp : ICrawlerService, IApp
    {
        private static readonly ILog Log = LogManager.GetLogger(
            MethodBase.GetCurrentMethod().DeclaringType);

        private readonly IMessageBus _messageBus;
        private readonly IDbContext _dbContext;
        private readonly IQueueManager _queueManager;
        private readonly IWarmCache _warmCache;

        /// <summary>
        /// Holds the date when this instance was created.
        /// Used to calculate uptime.
        /// </summary>
        private readonly DateTime _creationTime;

        // runs the crawler due to MySql-WCF issues
        private readonly Thread _crawlThread;

        public CrawlApp(
            IMessageBus messageBus,
            IDbContext dbContext,
            IQueueManager queueManager,
            IWarmCache warmCache)
        {
            Log.Debug("CrawlApp constructor");
            _messageBus = messageBus;
            _dbContext = dbContext;
            _queueManager = queueManager;
            _warmCache = warmCache;
            _creationTime = TestableDateTime.UtcNow;
            _crawlThread = new Thread(CrawlThreadStart);
        }

        void CrawlThreadStart(object parameter)
        {
            new StoryPollHistoryLogger(_messageBus, _dbContext);
            _queueManager.Start();
        }

        public string Echo(string msg)
        {
            return msg;
        }

        public TimeSpan GetUpTime()
        {
            return TestableDateTime.UtcNow.Subtract(_creationTime);
        }

        public void Run(string[] args)
        {
            Run(new Options
            {
                HostRemoteControlService = !args.Contains("-nohost"),
                SkipIngesters = args.Contains("-skipIngesters"),
                SkipPollers = args.Contains("-skipPollers"),
                UseBackgroundThreads = args.Contains("-useBackgroundThreads"),
                SkipCrawlerEventsForwarder = args.Contains("-skipCrawlerEventsForwarder")
            });
        }

        public void Run(Options options = null)
        {
            Log.Debug("Run");
            // make sure we have default options if they're null
            options = options ?? new Options();

            // hook into message bus
            if (!options.SkipCrawlerEventsForwarder)
            {
                new CrawlerEventsForwarder(_messageBus);
            }

            Log.Debug("Updating recent activity");
            _warmCache.UpdateRecentActivity();
            _messageBus.Subscribe<StoryCheckedMessage>(message =>
            {
                if (message.Changes != UpdateResult.NoChanges)
                {
                    _warmCache.UpdateRecentActivity();
                }
            });

            // host the service
            if (options.HostRemoteControlService)
            {
                HostRemoteControlService();
            }

            // install Ctrl+C handler
            Console.CancelKeyPress += OnCancelKeyPress;

            // main polling loop
            // in a separate thread because of MySql complaining
            // about nested transactions due to interference
            // with WCF service host.
            _queueManager.InfiniteLoop = true;
            //CrawlThreadStart(null);
            _crawlThread.Start();
            Console.WriteLine("Crawl started. Press Ctrl+C to exit...");
        }

        private void OnCancelKeyPress(object sender, ConsoleCancelEventArgs e)
        {
        }

        private void HostRemoteControlService()
        {
            Log.Debug("Opening web services host");
            var webServicesHost = new WebServicesHost(
                ServiceLocator.Current,
                new JsonSerializer(),
                BuzzStatsConfigurationSection.Current.Crawler.WebServicesPrefix);
            webServicesHost.DiscoverHandlers();
            webServicesHost.Start();
            Log.Debug("Web services host open");

//
//			_stoppable.Register(() =>
//			{
//				Log.Debug("Shuting down hosts due to closing time");
//				try
//				{
//					crawlerService.Close();
//				}
//				catch (Exception ex)
//				{
//					Log.Error("Error closing Crawler Service", ex);
//				}
//
//				try
//				{
//					webApiService.Close();
//				}
//				catch (Exception ex)
//				{
//					Log.Error("Error closing Web API Service", ex);
//				}
//			});
        }

        public class Options
        {
            private bool _hostRemoteControlService = true;

            public bool HostRemoteControlService
            {
                get
                {
                    return _hostRemoteControlService;
                }
                set
                {
                    _hostRemoteControlService = value;
                }
            }

            public bool SkipIngesters { get; set; }

            public bool SkipPollers { get; set; }

            public bool UseBackgroundThreads { get; set; }

            public bool SkipCrawlerEventsForwarder { get; set; }
        }
    }
}
