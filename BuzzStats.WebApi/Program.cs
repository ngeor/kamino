using System;
using System.Threading;
using BuzzStats.WebApi.Crawl;
using BuzzStats.WebApi.IoC;
using BuzzStats.WebApi.Parsing;
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

            ListingTask listingTask = ContainerHolder.Container.GetInstance<ListingTask>();

            Console.CancelKeyPress += (sender, eventArgs) => done.Set();
            
            // Start OWIN host 
            using (WebApp.Start<Startup>(url: baseAddress))
            {
                int page = 0;
                Log.InfoFormat("Server listening at {0}", baseAddress);
                TaskLoop.RunForEver(async () =>
                {
                    foreach (StoryListing storyListing in Enum.GetValues(typeof(StoryListing)))
                    {
                        await listingTask.RunOnce(storyListing, page);
                    }

                    page++;
                });

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