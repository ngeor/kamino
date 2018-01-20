using BuzzStats.Parsing.DTOs;
using BuzzStats.WebApi.DTOs;

namespace BuzzStats.WebApi.Crawl
{
    public interface IStoryProcessTopic
    {
        void Post(StoryListingSummary storyListingSummary);
    }
}
