using BuzzStats.StorageWebApi.Entities;
using FluentNHibernate.Mapping;

namespace BuzzStats.StorageWebApi.ClassMaps
{
    public class StoryMap : ClassMap<StoryEntity>
    {
        public StoryMap()
        {
            Table("Story");
            Id(story => story.Id);
            Map(story => story.StoryId).Unique().Not.Update().Not.Nullable();
            Map(story => story.Title).Not.Nullable();
            Map(story => story.Url);
            Map(story => story.Username).Not.Nullable();
            Map(story => story.CreatedAt).Not.Nullable().Index("IX_Story_CreatedAt");
            Map(story => story.Category).Not.Nullable();
        }
    }
}