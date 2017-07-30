// --------------------------------------------------------------------------------
// <copyright file="CrawlApp.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2014
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/09/04
// * Time: 1:27 μμ
// --------------------------------------------------------------------------------

using System;
using System.Reflection;
using log4net;
using NGSoftware.Common;
using BuzzStats.Services;
using BuzzStats.Tasks;

namespace BuzzStats.Crawl
{
    public class CrawlApp : ICrawlerService, IApp
    {
        private static readonly ILog Log = LogManager.GetLogger(
            MethodBase.GetCurrentMethod().DeclaringType);

        /// <summary>
        /// Holds the date when this instance was created.
        /// Used to calculate uptime.
        /// </summary>
        private readonly DateTime _creationTime;

        public CrawlApp()
        {
            Log.Debug("CrawlApp constructor");
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
            Run();
        }

        public void Run()
        {
            Log.Debug("Run");

            // main polling loop
            // in a separate thread because of MySql complaining
            // about nested transactions due to interference
            // with WCF service host.
            Console.WriteLine("Crawl started. Press Ctrl+C to exit...");
        }
    }
}