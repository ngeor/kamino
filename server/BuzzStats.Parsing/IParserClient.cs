using BuzzStats.DTOs;
using BuzzStats.Parsing.DTOs;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace BuzzStats.Parsing
{
    public interface IParserClient
    {
        Task<IEnumerable<StoryListingSummary>> Listing(StoryListing storyListing, int page);
        Task<Story> Story(int storyId);
    }
}