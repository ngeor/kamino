using System.Threading.Tasks;
using BuzzStats.DTOs;

namespace BuzzStats.StoryUpdater
{
    public interface IRepository
    {
        Task<int?> OldestCheckedStory();
        Task RegisterChangeEvent(StoryEvent storyEvent);
        Task UpdateLastCheckedDate(int storyId);
    }
}