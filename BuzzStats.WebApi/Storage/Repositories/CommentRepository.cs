using System.Collections.Generic;
using BuzzStats.WebApi.Storage.Entities;
using NHibernate;
using NHibernate.Criterion;

namespace BuzzStats.WebApi.Storage.Repositories
{
    public class CommentRepository : ICommentRepository
    {
        private readonly ISession _session;

        public CommentRepository(ISession session)
        {
            _session = session;
        }

        public virtual CommentEntity GetByCommentId(int commentId)
        {
            var criteria = _session.CreateCriteria<CommentEntity>();
            criteria = criteria.Add(Restrictions.Eq("CommentId", commentId));
            return criteria.UniqueResult<CommentEntity>();
        }

        public virtual IList<CommentEntity> GetRecent()
        {
            var criteria = _session.CreateCriteria<CommentEntity>();
            criteria = criteria.AddOrder(Order.Desc("CreatedAt"));
            criteria = criteria.SetMaxResults(20);
            return criteria.List<CommentEntity>();
        }
    }
}
