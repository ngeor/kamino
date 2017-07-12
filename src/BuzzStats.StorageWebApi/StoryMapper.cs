using BuzzStats.StorageWebApi.DTOs;
using BuzzStats.StorageWebApi.Entities;

namespace BuzzStats.StorageWebApi
{
    /// <summary>
    /// Maps story DTOs to story entities.
    /// </summary>
    public class StoryMapper
    {
        public virtual StoryEntity ToStoryEntity(Story story)
        {
            var storyEntity = new StoryEntity
            {
                Title = story.Title,
                StoryId = story.StoryId,
                Url = story.Url,
                Username = story.Username,
                CreatedAt = story.CreatedAt,
                Category = story.Category,
            };

            return storyEntity;
        }
    }
}