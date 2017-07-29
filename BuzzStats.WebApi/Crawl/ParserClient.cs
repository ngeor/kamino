using System.Net.Http;
using System.Threading.Tasks;
using BuzzStats.WebApi.DTOs;
using log4net;
using Newtonsoft.Json;
using NGSoftware.Common.Configuration;

namespace BuzzStats.WebApi.Crawl
{
    public class ParserClient : IParserClient
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof(ParserClient));
        private readonly IAppSettings _appSettings;

        public ParserClient(IAppSettings appSettings)
        {
            _appSettings = appSettings;
        }

        public virtual async Task<StoryListingSummary[]> Home()
        {
            string homeUrl = HomeUrl();
            Log.InfoFormat("Calling {0}", homeUrl);
            HttpClient client = new HttpClient();
            string result = await client.GetStringAsync(homeUrl);
            var storyListingSummaries = JsonConvert.DeserializeObject<StoryListingSummary[]>(result);
            return storyListingSummaries;
        }

        public virtual async Task<Story> Story(int storyId)
        {
            var storyUrl = StoryUrl(storyId);
            Log.InfoFormat("Calling {0}", storyUrl);
            HttpClient client = new HttpClient();
            string result = await client.GetStringAsync(storyUrl);
            return JsonConvert.DeserializeObject<Story>(result);
        }
        
        private string HomeUrl()
        {
            return ParserWebApiUrl() + "/api/listing/home";
        }

        private string StoryUrl(int storyId)
        {
            return ParserWebApiUrl() + "/api/story/" + storyId;
        }

        private string ParserWebApiUrl() => _appSettings["ParserWebApiUrl"];
    }
}