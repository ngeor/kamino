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
    }
}