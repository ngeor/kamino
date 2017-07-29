using System.Collections.Generic;
using System.Threading.Tasks;
using BuzzStats.WebApi.DTOs;

namespace BuzzStats.WebApi.Parsing
{
    public interface IParserClient
    {
        Task<IEnumerable<StoryListingSummary>> Home();
        Task<Story> Story(int storyId);
    }
}