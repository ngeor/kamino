using System.Collections.Generic;
using BuzzStats.WebApi.Storage.Entities;
using NHibernate;
using NHibernate.Criterion;

namespace BuzzStats.WebApi.Storage.Repositories
{
    public class CommentRepository
    {
        public virtual CommentEntity GetByCommentId(ISession session, int commentId)
        {
            var criteria = session.CreateCriteria<CommentEntity>();
            criteria = criteria.Add(Restrictions.Eq("CommentId", commentId));
            return criteria.UniqueResult<CommentEntity>();
        }

        public virtual IList<CommentEntity> GetRecent(ISession session)
        {
            var criteria = session.CreateCriteria<CommentEntity>();
            criteria = criteria.AddOrder(Order.Desc("CreatedAt"));
            criteria = criteria.SetMaxResults(20);
            return criteria.List<CommentEntity>();
        }
    }
}