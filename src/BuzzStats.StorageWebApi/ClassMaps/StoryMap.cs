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
            Map(story => story.Host);
            Map(story => story.Category).Not.Nullable();
            Map(story => story.VoteCount).Not.Nullable();
            Map(story => story.Username).Not.Nullable();

            Map(story => story.CreatedAt).Not.Nullable().Index("IX_Story_CreatedAt");
            Map(story => story.DetectedAt).Not.Update().Not.Nullable();
            Map(story => story.LastCheckedAt).Not.Nullable().Index("IX_Story_LastCheckedAt");
            Map(story => story.LastModifiedAt).Not.Nullable().Index("IX_Story_LastModifiedAt");
            Map(story => story.TotalUpdates).Not.Nullable();
            Map(story => story.TotalChecks).Not.Nullable().Index("IX_Story_TotalChecks");
            Map(story => story.LastCommentedAt).Index("IX_Story_LastCommentedAt");
            Map(story => story.RemovedAt).Index("IX_Story_RemovedAt");
        }
    }
}