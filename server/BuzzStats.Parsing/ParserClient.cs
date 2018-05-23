using System;
using System.Collections.Generic;
using System.Net.Http;
using System.Threading.Tasks;
using BuzzStats.DTOs;
using BuzzStats.Parsing.DTOs;
using Microsoft.Extensions.Logging;

namespace BuzzStats.Parsing
{
    public class ParserClient : IParserClient
    {
        private readonly IUrlProvider _urlProvider;
        private readonly IParser _parser;
        private readonly ILogger logger;

        public ParserClient(IUrlProvider urlProvider, IParser parser, ILogger logger)
        {
            _urlProvider = urlProvider;
            _parser = parser;
            this.logger = logger;
        }

        public virtual async Task<IEnumerable<StoryListingSummary>> Listing(StoryListing storyListing, int page)
        {
            HttpClient client = new HttpClient();
            var requestUri = _urlProvider.ListingUrl(storyListing, page);
            logger.LogInformation("Calling {0}", requestUri);
            try
            {
                string htmlContents = await client.GetStringAsync(requestUri);
                return _parser.ParseListingPage(htmlContents);
            }
            catch (Exception ex)
            {
                logger.LogError(ex.Message, ex);
                throw;
            }
        }

        public virtual async Task<Story> Story(int storyId)
        {
            HttpClient client = new HttpClient();
            var requestUri = _urlProvider.StoryUrl(storyId);
            logger.LogInformation("Calling {0}", requestUri);
            string storyPageContents = await client.GetStringAsync(requestUri);
            return _parser.ParseStoryPage(storyPageContents, storyId);
        }
    }
}