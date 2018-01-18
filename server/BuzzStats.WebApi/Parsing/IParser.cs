using System.Collections.Generic;
using BuzzStats.WebApi.DTOs;

namespace BuzzStats.WebApi.Parsing
{
    public interface IParser
    {
        Story ParseStoryPage(string storyPageContents, int requestedStoryId);
        IEnumerable<StoryListingSummary> ParseListingPage(string htmlUpcomingPage);
    }
}