using FluentNHibernate.Mapping;
using BuzzStats.Data.NHibernate.Entities;

namespace BuzzStats.Data.NHibernate.ClassMaps
{
    public class CommentMap : ClassMap<CommentEntity>
    {
        public CommentMap()
        {
            Table("Comment");
            Id(comment => comment.Id);
            Map(comment => comment.CommentId).Unique().Not.Update().Not.Nullable();
            References(comment => comment.Story).Not.Nullable();
            References(comment => comment.ParentComment);
            Map(comment => comment.Username).Not.Nullable();
            Map(comment => comment.VotesUp).Not.Nullable();
            Map(comment => comment.VotesDown).Not.Nullable();
            Map(comment => comment.IsBuried).Not.Nullable();

            Map(comment => comment.CreatedAt).Not.Nullable();
            Map(comment => comment.DetectedAt).Not.Update().Not.Nullable();
        }
    }
}