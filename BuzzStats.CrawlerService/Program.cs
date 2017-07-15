using System;
using System.Collections.Generic;
using System.Web.Http;
using Microsoft.Owin.Hosting;

namespace BuzzStats.CrawlerService
{
    internal class Program
    {
        public static void Main(string[] args)
        {
            const string baseAddress = "http://localhost:9001/";
            ListingTask listingTask = new ListingTask(new ParserClient(), new StorageClient());

            // Start OWIN host 
            using (WebApp.Start<Startup>(url: baseAddress))
            {
                Console.WriteLine("Server listening at port 9001");
                TaskLoop.RunForEver(() => listingTask.RunOnce());
                Console.ReadLine();
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