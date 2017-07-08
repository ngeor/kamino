using BuzzStats.StorageWebApi.Entities;
using FluentNHibernate.Mapping;

namespace BuzzStats.StorageWebApi.ClassMaps
{
    public class CommentVoteMap : ClassMap<CommentVoteEntity>
    {
        public CommentVoteMap()
        {
            Table("CommentVote");
            Id(x => x.Id);
            References(x => x.Comment).Index("votes").Not.Nullable();

            Map(x => x.VotesUp).Index("votes").Not.Nullable();
            Map(x => x.VotesDown).Index("votes").Not.Nullable();
            Map(x => x.IsBuried).Index("votes").Not.Nullable();

            Map(x => x.CreatedAt).Not.Nullable();
        }
    }
}