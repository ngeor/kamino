using System;
using System.Threading;
using BuzzStats.WebApi.Crawl;
using BuzzStats.WebApi.Parsing;
using BuzzStats.WebApi.Storage;
using log4net;
using Microsoft.Owin.Hosting;
using NGSoftware.Common.Configuration;

namespace BuzzStats.WebApi
{
    public class Program
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof(Program));
        
        public static void Main(string[] args)
        {
            ManualResetEventSlim done = new ManualResetEventSlim(false);
            IAppSettings appSettings = AppSettingsFactory.DefaultWithEnvironmentOverride();
            string baseAddress = appSettings["WebApiUrl"];

            ListingTask listingTask = new ListingTask(
                new ParserClient(appSettings),
                new StorageClient(appSettings));

            Console.CancelKeyPress += (sender, eventArgs) => done.Set();
            
            // Start OWIN host 
            using (WebApp.Start<Startup>(url: baseAddress))
            {
                Log.InfoFormat("Server listening at {0}", baseAddress);
                TaskLoop.RunForEver(() => listingTask.RunOnce());

                if (!Console.IsInputRedirected)
                {
                    Console.ReadLine();
                    done.Set();
                }
                
                done.Wait();
                Log.Info("Server exiting");
            }
        }
    }
}