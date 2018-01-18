using System;

namespace BuzzStats.WebApi.Storage.Entities
{
    public class RecentActivityEntity
    {
        public virtual int Id { get; set; }
        public virtual StoryEntity Story { get; set; }
        public virtual CommentEntity Comment { get; set; }
        public virtual StoryVoteEntity StoryVote { get; set; }
        public virtual DateTime CreatedAt { get; set; }
    }
}