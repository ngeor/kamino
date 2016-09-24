using FluentNHibernate.Mapping;
using BuzzStats.Data.NHibernate.Entities;

namespace BuzzStats.Data.NHibernate.ClassMaps
{
    public class StoryPollHistoryMap : ClassMap<StoryPollHistoryEntity>
    {
        public StoryPollHistoryMap()
        {
            Table("StoryPollHistory");
            Id();
            References(storyPollHistory => storyPollHistory.Story).Not.Update().Nullable();
            Map(storyPollHistory => storyPollHistory.SourceId).Not.Update().Nullable();
            Map(storyPollHistory => storyPollHistory.CheckedAt).Not.Update().Not.Nullable();
            Map(storyPollHistory => storyPollHistory.HadChanges).Not.Update().Nullable();
        }
    }
}
