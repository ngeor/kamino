using BuzzStats.WebApi.Storage.Entities;
using FluentNHibernate.Mapping;

namespace BuzzStats.WebApi.Storage.ClassMaps
{
    public class StoryVoteMap : ClassMap<StoryVoteEntity>
    {
        public StoryVoteMap()
        {
            Table("StoryVote");
            Id(x => x.Id);
            References(x => x.Story).UniqueKey("uniqueVoter").Not.Nullable();
            Map(x => x.Username).UniqueKey("uniqueVoter").Not.Nullable();
        }
    }
}