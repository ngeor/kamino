using System.Configuration;
using System.Net.Http;
using System.Threading.Tasks;
using BuzzStats.CrawlerService.DTOs;
using Newtonsoft.Json;

namespace BuzzStats.CrawlerService
{
    public class ParserClient
    {
        public virtual async Task<StoryListingSummary[]> Home()
        {
            string homeUrl = HomeUrl();
            HttpClient client = new HttpClient();
            string result = await client.GetStringAsync(homeUrl);
            var storyListingSummaries = JsonConvert.DeserializeObject<StoryListingSummary[]>(result);
            return storyListingSummaries;
        }

        public virtual async Task<Story> Story(int storyId)
        {
            var storyUrl = StoryUrl(storyId);
            HttpClient client = new HttpClient();
            string result = await client.GetStringAsync(storyUrl);
            return JsonConvert.DeserializeObject<Story>(result);
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
}