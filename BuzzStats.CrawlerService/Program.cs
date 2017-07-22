using System;
using System.Collections.Generic;
using System.Threading;
using System.Web.Http;
using log4net;
using Microsoft.Owin.Hosting;

namespace BuzzStats.CrawlerService
{
    internal class Program
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof(Program));
        
        public static void Main(string[] args)
        {
            ManualResetEventSlim done = new ManualResetEventSlim(false);
            const string baseAddress = "http://localhost:9001/";
            ListingTask listingTask = new ListingTask(new ParserClient(), new StorageClient());

            Console.CancelKeyPress += (sender, eventArgs) => done.Set();
            
            // Start OWIN host 
            using (WebApp.Start<Startup>(url: baseAddress))
            {
                Log.Info("Server listening at port 9001");
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