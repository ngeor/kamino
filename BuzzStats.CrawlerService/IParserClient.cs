using System.Threading.Tasks;
using BuzzStats.CrawlerService.DTOs;

namespace BuzzStats.CrawlerService
{
    public interface IParserClient
    {
        Task<StoryListingSummary[]> Home();
        Task<Story> Story(int storyId);
    }
}