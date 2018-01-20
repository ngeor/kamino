using System;
using System.Linq;
using System.Threading.Tasks;
using BuzzStats.Parsing;
using log4net;

namespace BuzzStats.WebApi.Crawl
{
    public class ListingTask
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof(ListingTask));
        private readonly IParserClient _parserClient;
        private readonly IStoryProcessTopic _storyProcessTopic;

        public ListingTask(IParserClient parserClient, IStoryProcessTopic storyProcessTopic)
        {
            _parserClient = parserClient;
            _storyProcessTopic = storyProcessTopic;
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
                    // TODO The receiving queue should de-dupe stories.
                    _storyProcessTopic.Post(storyListingSummary);
                }
            }
            catch (Exception ex)
            {
                Log.Error(ex.Message, ex);
            }
        }
    }
}