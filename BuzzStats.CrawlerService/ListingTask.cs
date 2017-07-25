using System;
using System.Threading.Tasks;
using BuzzStats.CrawlerService.DTOs;
using log4net;

namespace BuzzStats.CrawlerService
{
    public class ListingTask
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof(ListingTask));
        private readonly ParserClient _parserClient;
        private readonly StorageClient _storageClient;

        public ListingTask(ParserClient parserClient, StorageClient storageClient)
        {
            _parserClient = parserClient;
            _storageClient = storageClient;
        }

        public async Task RunOnce()
        {
            Log.Info("Begin task");
            try
            {
                // TODO crawl other pages too
                // TODO crawl stories to see if they have changed (perhaps on a separate microservice)
                var storyListingSummaries = await _parserClient.Home();
                Log.InfoFormat("Received {0} stories", storyListingSummaries.Length);

                foreach (var storyListingSummary in storyListingSummaries)
                {
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
            await _storageClient.Save(parsedStory);
            return parsedStory;
        }
    }
}