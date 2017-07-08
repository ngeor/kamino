using System;
using System.Collections.Generic;
using System.Configuration;
using System.Net.Http;
using System.Threading.Tasks;
using System.Web.Http;
using log4net;
using Microsoft.Owin.Hosting;
using Newtonsoft.Json;
using Owin;

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

    class StoryListingSummary
    {
        public int StoryId { get; set; }

        public int? VoteCount { get; set; }
    }

    public class Story
    {
        public int StoryId { get; set; }

        public string Title { get; set; }

        public bool IsRemoved { get; set; }

        public int Category { get; set; }

        public string Url { get; set; }

        public DateTime CreatedAt { get; set; }

        public string Username { get; set; }

        public string[] Voters { get; set; }

        public Comment[] Comments { get; set; }
    }
    
    public class Comment
    {
        public int CommentId { get; set; }

        public string Username { get; set; }

        public DateTime CreatedAt { get; set; }

        public int VotesUp { get; set; }

        public int VotesDown { get; set; }

        public bool IsBuried { get; set; }

        public Comment[] Comments { get; set; }
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
    }

    public class Startup
    {
        // This code configures Web API. The Startup class is specified as a type
        // parameter in the WebApp.Start method.
        public void Configuration(IAppBuilder appBuilder)
        {
            // Configure Web API for self-host. 
            HttpConfiguration config = new HttpConfiguration();
            config.Routes.MapHttpRoute(
                name: "DefaultApi",
                routeTemplate: "api/{controller}/{id}",
                defaults: new {id = RouteParameter.Optional}
            );

            appBuilder.UseWebApi(config);
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