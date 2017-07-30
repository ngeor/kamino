using System;
using System.Collections.Generic;
using System.Net.Http;
using System.Threading.Tasks;
using BuzzStats.WebApi.DTOs;
using log4net;

namespace BuzzStats.WebApi.Parsing
{
    public class ParserClient : IParserClient
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof(ParserClient));
        private readonly IUrlProvider _urlProvider;

        public ParserClient(IUrlProvider urlProvider)
        {
            _urlProvider = urlProvider;
        }

        public virtual async Task<IEnumerable<StoryListingSummary>> Listing(StoryListing storyListing, int page)
        {
            Parser parser = new Parser();
            HttpClient client = new HttpClient();
            var requestUri = _urlProvider.ListingUrl(storyListing, page);
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
            var requestUri = _urlProvider.StoryUrl(storyId);
            Log.InfoFormat("Calling {0}", requestUri);
            string storyPageContents = await client.GetStringAsync(requestUri);
            return parser.ParseStoryPage(storyPageContents, storyId);
        }
    }
}