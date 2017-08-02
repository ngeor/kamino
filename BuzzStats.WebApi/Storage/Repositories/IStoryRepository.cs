using BuzzStats.WebApi.Storage.Entities;

namespace BuzzStats.WebApi.Storage.Repositories
{
    public interface IStoryRepository
    {
        StoryEntity GetByStoryId(int storyId);
    }
}
