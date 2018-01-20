using System.Collections.Generic;
using BuzzStats.Parsing.DTOs;

namespace BuzzStats.Parsing
{
    public interface IParser
    {
        Story ParseStoryPage(string storyPageContents, int requestedStoryId);
        IEnumerable<StoryListingSummary> ParseListingPage(string htmlUpcomingPage);
    }
}