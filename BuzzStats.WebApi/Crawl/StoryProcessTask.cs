using System.Threading.Tasks;
using BuzzStats.WebApi.DTOs;
using BuzzStats.WebApi.Parsing;
using BuzzStats.WebApi.Storage;
using log4net;

namespace BuzzStats.WebApi.Crawl
{
    public class StoryProcessTask
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof(StoryProcessTask));

        private readonly IParserClient _parserClient;
        private readonly IStorageClient _storageClient;
        private readonly IAsyncQueue<StoryListingSummary> _queue;

        public StoryProcessTask(IParserClient parserClient, IStorageClient storageClient, IAsyncQueue<StoryListingSummary> queue)
        {
            _parserClient = parserClient;
            _storageClient = storageClient;
            _queue = queue;
        }

        public async Task<Story> RunOnce()
        {
            return await ProcessStory(_queue.Pop());
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