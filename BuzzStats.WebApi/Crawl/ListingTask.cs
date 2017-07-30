using System;
using System.Linq;
using System.Threading.Tasks;
using BuzzStats.WebApi.DTOs;
using BuzzStats.WebApi.Parsing;
using BuzzStats.WebApi.Storage;
using log4net;

namespace BuzzStats.WebApi.Crawl
{
    public class ListingTask
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof(ListingTask));
        private readonly IParserClient _parserClient;
        private readonly IStorageClient _storageClient;

        public ListingTask(IParserClient parserClient, IStorageClient storageClient)
        {
            _parserClient = parserClient;
            _storageClient = storageClient;
        }

        public async Task RunOnce(StoryListing storyListing, int page)
        {
            Log.InfoFormat("Begin task for story listing {0}, page {1}", storyListing, page);
            try
            {
                // TODO crawl stories to see if they have changed (perhaps on a separate microservice)
                var storyListingSummaries = (await _parserClient.Listing(storyListing, page)).ToArray();
                Log.InfoFormat("Received {0} stories", storyListingSummaries.Length);

                foreach (var storyListingSummary in storyListingSummaries)
                {
                    // TODO instead of direct processing, message that these stories
                    // need to be processed. That receiving queue should de-dupe stories.
                    await ProcessStory(storyListingSummary);
                }
            }
            catch (Exception ex)
            {
                Log.Error(ex.Message, ex);
            }
        }

        private async Task<Story> ProcessStory(StoryListingSummary storyListingSummary)
        {
            var storyId = storyListingSummary.StoryId;
            Log.InfoFormat("Getting story id {0}", storyId);
            var parsedStory = await _parserClient.Story(storyId);
            Log.InfoFormat("Parsed story {0}", parsedStory.Title);
            _storageClient.Save(parsedStory);
            return parsedStory;
        }
    }
}