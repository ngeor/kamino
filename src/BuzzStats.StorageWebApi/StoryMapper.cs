using System.Linq;
using BuzzStats.StorageWebApi.DTOs;
using BuzzStats.StorageWebApi.Entities;

namespace BuzzStats.StorageWebApi
{
    /// <summary>
    /// Maps story DTOs to story entities.
    /// </summary>
    public class StoryMapper
    {
        public virtual StoryEntity ToStoryEntity(Story story) => new StoryEntity
        {
            Title = story.Title,
            StoryId = story.StoryId,
            Url = story.Url,
            Username = story.Username,
            CreatedAt = story.CreatedAt,
            Category = story.Category,
        };

        public virtual StoryVoteEntity[] ToStoryVoteEntities(Story story, StoryEntity storyEntity) =>
            (story.Voters ?? Enumerable.Empty<string>()).Select(v => new StoryVoteEntity
            {
                Story = storyEntity,
                Username = v
            }).ToArray();
    }
}