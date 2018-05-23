using BuzzStats.DTOs;
using BuzzStats.Parsing.DTOs;
using System.Threading.Tasks;

namespace BuzzStats.Parsing
{
    public interface IParserClient
    {
        Task<StoryListingSummaries> ListingAsync(StoryListing storyListing, int page);
        Task<Story> StoryAsync(int storyId);
    }
}