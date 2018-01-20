using System;
using System.Threading;
using BuzzStats.WebApi.Crawl;
using BuzzStats.WebApi.IoC;
using BuzzStats.Parsing;
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

            Console.CancelKeyPress += (sender, eventArgs) => done.Set();
            
            // Start OWIN host 
            using (WebApp.Start<Startup>(url: baseAddress))
            {
                Log.InfoFormat("Server listening at {0}", baseAddress);

                RunListingTasks();
                RunStoryProcessTask();

                if (!Console.IsInputRedirected)
                {
                    Console.ReadLine();
                    done.Set();
                }
                
                done.Wait();
                Log.Info("Server exiting");
            }
        }

        private static void RunListingTasks()
        {
            ListingTask listingTask = ContainerHolder.Container.GetInstance<ListingTask>();
            int page = 0;
            TaskLoop.RunForEver(async () =>
            {
                // TODO: when the story processor queue is full, stop
                // TODO: when the story processor reports updates, start over from page 0
                foreach (StoryListing storyListing in Enum.GetValues(typeof(StoryListing)))
                {
                    await listingTask.RunOnce(storyListing, page);
                }

                page++;
            });
        }

        private static void RunStoryProcessTask()
        {
            StoryProcessTask storyProcessTask = ContainerHolder.Container.GetInstance<StoryProcessTask>();
            TaskLoop.RunForEver(async () =>
            {
                await storyProcessTask.RunOnce();
            });
        }
    }
}