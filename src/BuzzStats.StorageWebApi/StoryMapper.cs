using BuzzStats.StorageWebApi.DTOs;
using BuzzStats.StorageWebApi.Entities;

namespace BuzzStats.StorageWebApi
{
    /// <summary>
    /// Maps story DTOs to story entities.
    /// </summary>
    public class StoryMapper
    {
        public StoryEntity ToStoryEntity(Story story)
        {
            var storyEntity = new StoryEntity
            {
                Title = story.Title,
                StoryId = story.StoryId,
                Username = story.Username,
                Url = story.Url,
                Category = story.Category,
            };

            return storyEntity;
        }
    }
}