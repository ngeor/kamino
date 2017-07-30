using BuzzStats.WebApi.Storage.Entities;
using FluentNHibernate.Mapping;

namespace BuzzStats.WebApi.Storage.ClassMaps
{
    public class RecentActivityMap : ClassMap<RecentActivityEntity>
    {
        public RecentActivityMap()
        {
            Table("RecentActivity");
            Id(x => x.Id);
            
            Component(x => x.Story, m =>
            {
                m.Map(x => x.StoryId);
                m.Map(x => x.Title);
                m.Map(x => x.Username);
            }).ColumnPrefix("Story");
            
            Component(x => x.Comment, m =>
            {
                m.Map(x => x.CommentId);
                m.Map(x => x.Username);
            }).ColumnPrefix("Comment");
            
            Component(x => x.StoryVote, m =>
            {
                m.Map(x => x.Username);
            }).ColumnPrefix("StoryVote");

            Map(x => x.CreatedAt).Not.Nullable();
        }
    }
}