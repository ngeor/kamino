using BuzzStats.DTOs;
using BuzzStats.Parsing.DTOs;

namespace BuzzStats.Parsing
{
    public interface IParser
    {
        Story ParseStoryPage(string storyPageContents, int requestedStoryId);
        StoryListingSummaries ParseListingPage(string htmlUpcomingPage);
    }
}