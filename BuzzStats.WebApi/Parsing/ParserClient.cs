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
        private readonly IParser _parser;
        
        public ParserClient(IUrlProvider urlProvider, IParser parser)
        {
            _urlProvider = urlProvider;
            _parser = parser;
        }

        public virtual async Task<IEnumerable<StoryListingSummary>> Listing(StoryListing storyListing, int page)
        {
            HttpClient client = new HttpClient();
            var requestUri = _urlProvider.ListingUrl(storyListing, page);
            Log.InfoFormat("Calling {0}", requestUri);
            try
            {
                string htmlContents = await client.GetStringAsync(requestUri);
                return _parser.ParseListingPage(htmlContents);
            }
            catch (Exception ex)
            {
                Log.Error(ex.Message, ex);
                throw;
            }
        }

        public virtual async Task<Story> Story(int storyId)
        {
            HttpClient client = new HttpClient();
            var requestUri = _urlProvider.StoryUrl(storyId);
            Log.InfoFormat("Calling {0}", requestUri);
            string storyPageContents = await client.GetStringAsync(requestUri);
            return _parser.ParseStoryPage(storyPageContents, storyId);
        }
    }
}