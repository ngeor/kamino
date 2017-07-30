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
using NGSoftware.Common;
using NGSoftware.Common.Messaging;
using BuzzStats.Configuration;
using BuzzStats.Data;
using BuzzStats.Persister;
using BuzzStats.Services;
using BuzzStats.Tasks;

namespace BuzzStats.Crawl
{
    [CommandLine("-skipIngesters -skipPollers -useBackgroundThreads -skipCrawlerEventsForwarder",
        "Starts crawling.")]
    public class CrawlApp : ICrawlerService, IApp
    {
        private static readonly ILog Log = LogManager.GetLogger(
            MethodBase.GetCurrentMethod().DeclaringType);

        private readonly IMessageBus _messageBus;

        /// <summary>
        /// Holds the date when this instance was created.
        /// Used to calculate uptime.
        /// </summary>
        private readonly DateTime _creationTime;

        public CrawlApp(
            IMessageBus messageBus)
        {
            Log.Debug("CrawlApp constructor");
            _messageBus = messageBus;
            _creationTime = TestableDateTime.UtcNow;
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

            // main polling loop
            // in a separate thread because of MySql complaining
            // about nested transactions due to interference
            // with WCF service host.
            Console.WriteLine("Crawl started. Press Ctrl+C to exit...");
        }

        public class Options
        {
            public bool SkipIngesters { get; set; }

            public bool SkipPollers { get; set; }

            public bool UseBackgroundThreads { get; set; }

            public bool SkipCrawlerEventsForwarder { get; set; }
        }
    }
}