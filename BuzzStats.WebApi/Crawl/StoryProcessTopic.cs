using BuzzStats.WebApi.DTOs;

namespace BuzzStats.WebApi.Crawl
{
    public class StoryProcessTopic : IStoryProcessTopic
    {
        private readonly IAsyncQueue<StoryListingSummary> _queue;

        public StoryProcessTopic(IAsyncQueue<StoryListingSummary> queue)
        {
            _queue = queue;
        }

        public void Post(StoryListingSummary storyListingSummary)
        {
            _queue.Push(storyListingSummary);
        }
    }
}