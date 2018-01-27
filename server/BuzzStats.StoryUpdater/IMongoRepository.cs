using System.Threading.Tasks;
using BuzzStats.DTOs;

namespace BuzzStats.StoryUpdater
{
    public interface IMongoRepository
    {
        Task<StoryHistory> OldestCheckedStory();
        Task RegisterChangeEvent(StoryEvent storyEvent);
        Task UpdateLastCheckedDate(int storyId);
    }
}