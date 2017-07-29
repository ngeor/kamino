using System;
using System.Collections.Generic;
using System.Configuration;
using System.Net.Http;
using System.Threading.Tasks;
using BuzzStats.WebApi.DTOs;
using log4net;
using NGSoftware.Common.Configuration;

namespace BuzzStats.WebApi.Parsing
{
    public class ParserClient : IParserClient
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof(ParserClient));
        private readonly IAppSettings _appSettings;

        public ParserClient(IAppSettings appSettings)
        {
            _appSettings = appSettings;
        }

        public virtual async Task<IEnumerable<StoryListingSummary>> Home()
        {
            Parser parser = new Parser();
            HttpClient client = new HttpClient();
            string path;
            var id = StoryListing.Home;
            switch (id)
            {
                case StoryListing.Home:
                    path = "";
                    break;
                case StoryListing.Upcoming:
                    path = "upcoming.php";
                    break;
                default:
                    path = "";
                    break;
            }

            var requestUri = ConfigurationManager.AppSettings["BuzzServerUrl"] + path;
            Log.InfoFormat("Calling {0}", requestUri);
            try
            {
                string htmlContents = await client.GetStringAsync(requestUri);
                return parser.ParseListingPage(htmlContents);
            }
            catch (Exception ex)
            {
                Log.Error(ex.Message, ex);
                throw;
            }

        }

        public virtual async Task<Story> Story(int storyId)
        {
            Parser parser = new Parser();
            HttpClient client = new HttpClient();
            string storyPageContents =
                await client.GetStringAsync(_appSettings["BuzzServerUrl"] + "story.php?id=" + storyId);
            return parser.ParseStoryPage(storyPageContents, storyId);
        }
    }
}