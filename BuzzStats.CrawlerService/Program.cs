using System;
using System.Collections.Generic;
using System.Threading;
using System.Web.Http;
using log4net;
using Microsoft.Owin.Hosting;
using NGSoftware.Common.Configuration;

namespace BuzzStats.CrawlerService
{
    internal class Program
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof(Program));
        
        public static void Main(string[] args)
        {
            ManualResetEventSlim done = new ManualResetEventSlim(false);
            IAppSettings appSettings = AppSettingsFactory.DefaultWithEnvironmentOverride();
            string baseAddress = appSettings["CrawlerServiceUrl"];
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

    public class ValuesController : ApiController
    {
        // GET api/values 
        public IEnumerable<string> Get()
        {
            return new[] {"value1", "value2"};
        }

        // GET api/values/5 
        public string Get(int id)
        {
            return "value";
        }

        // POST api/values 
        public void Post([FromBody] string value)
        {
        }

        // PUT api/values/5 
        public void Put(int id, [FromBody] string value)
        {
        }

        // DELETE api/values/5 
        public void Delete(int id)
        {
        }
    }
}