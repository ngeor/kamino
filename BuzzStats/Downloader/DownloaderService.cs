using System.Net.Http;
using BuzzStats.Parsing;
using Newtonsoft.Json;

namespace BuzzStats.Downloader
{
    public class DownloaderService : IDownloaderService
    {
        private const string ParserServiceUrl = "http://localhost:9002/";

        public Story DownloadStory(string url, int storyId)
        {
            HttpClient client = new HttpClient();
            var httpResponseMessage = client.GetAsync(ParserServiceUrl + "api/story/" + storyId).Result;
            var result = httpResponseMessage.Content.ReadAsStringAsync().Result;
            var story = JsonConvert.DeserializeObject<Story>(result);
            return story;
        }

        public StoryListingSummary[] DownloadStories(string url)
        {
            HttpClient client = new HttpClient();
            // TODO don't pass the full url
            var httpResponseMessage = client.GetAsync(ParserServiceUrl + "api/" + url).Result;
            var result = httpResponseMessage.Content.ReadAsStringAsync().Result;
            var stories = JsonConvert.DeserializeObject<StoryListingSummary[]>(result);
            return stories;
        }
    }
}