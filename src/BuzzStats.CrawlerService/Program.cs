using System;
using System.Collections.Generic;
using System.Configuration;
using System.Net.Http;
using System.Threading.Tasks;
using System.Web.Http;
using BuzzStats.CrawlerService.DTOs;
using log4net;
using Microsoft.Owin.Hosting;
using Newtonsoft.Json;

namespace BuzzStats.CrawlerService
{
    internal class Program
    {
        public static void Main(string[] args)
        {
            const string baseAddress = "http://localhost:9001/";
            ListingTask listingTask = new ListingTask();

            // Start OWIN host 
            using (WebApp.Start<Startup>(url: baseAddress))
            {
                Console.WriteLine("Server listening at port 9001");
                Task.Run(() => listingTask.DoIt());
                Console.ReadLine();
            }
        }
    }

    public class ListingTask
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof(ListingTask));
        
        public async Task DoIt()
        {
            while (true)
            {
                Log.Info("Begin task");
                string homeUrl = HomeUrl();
                HttpClient client = new HttpClient();
                string result = await client.GetStringAsync(homeUrl);
                var storyListingSummaries = JsonConvert.DeserializeObject<StoryListingSummary[]>(result);
                Log.InfoFormat("Received {0} stories", storyListingSummaries.Length);

                foreach (var storyListingSummary in storyListingSummaries)
                {
                    await ProcessStory(storyListingSummary);
                }
                
                await Task.Delay(TimeSpan.FromSeconds(1));
            }
        }

        private async Task<Story> ProcessStory(StoryListingSummary storyListingSummary)
        {
            var storyId = storyListingSummary.StoryId;
            var storyUrl = StoryUrl(storyId);
            Log.InfoFormat("Getting url {0}", storyUrl);
            HttpClient client = new HttpClient();
            string result = await client.GetStringAsync(storyUrl);
            var parsedStory = JsonConvert.DeserializeObject<Story>(result);
            Log.InfoFormat("Parsed story {0}", parsedStory.Title);
            await StoreStory(parsedStory);
            return parsedStory;
        }

        private string HomeUrl()
        {
            return ConfigurationManager.AppSettings["ParserWebApiUrl"] + "/api/listing/home";
        }

        private string StoryUrl(int storyId)
        {
            return ConfigurationManager.AppSettings["ParserWebApiUrl"] + "/api/story/" + storyId;
        }

        private async Task StoreStory(Story story)
        {
            HttpClient client = new HttpClient();
            await client.PostAsJsonAsync(ConfigurationManager.AppSettings["StorageWebApiUrl"] + "/api/story", story);
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