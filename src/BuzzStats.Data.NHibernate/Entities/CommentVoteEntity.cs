using System;

namespace BuzzStats.Data.NHibernate.Entities
{
    public class CommentVoteEntity : IEntity
    {
        public virtual int Id { get; set; }

        public virtual CommentEntity Comment { get; set; }

        public virtual int VotesUp { get; set; }

        public virtual int VotesDown { get; set; }

        public virtual bool IsBuried { get; set; }

        public virtual DateTime CreatedAt { get; set; }
    }
}
