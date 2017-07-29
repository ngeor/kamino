using System.Threading.Tasks;
using BuzzStats.WebApi.DTOs;

namespace BuzzStats.WebApi
{
    public interface IParserClient
    {
        Task<StoryListingSummary[]> Home();
        Task<Story> Story(int storyId);
    }
}