using System;

namespace BuzzStats.Data.NHibernate.Entities
{
    public class CommentEntity : IEntity
    {
        public virtual int Id { get; set; }

        public virtual int CommentId { get; set; }

        public virtual StoryEntity Story { get; set; }

        public virtual string Username { get; set; }

        public virtual CommentEntity ParentComment { get; set; }

        public virtual DateTime CreatedAt { get; set; }

        public virtual int VotesUp { get; set; }

        public virtual int VotesDown { get; set; }

        public virtual bool IsBuried { get; set; }

        public virtual DateTime DetectedAt { get; set; }
    }
}