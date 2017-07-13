using BuzzStats.StorageWebApi.Entities;
using NHibernate;
using NHibernate.Criterion;

namespace BuzzStats.StorageWebApi.Repositories
{
    public class StoryVoteRepository
    {
        public virtual StoryVoteEntity Get(ISession session, StoryEntity story, string username)
        {
            var criteria = session.CreateCriteria<StoryVoteEntity>();
            criteria = criteria.Add(Restrictions.Eq("Story", story));
            criteria = criteria.Add(Restrictions.Eq("Username", username));
            return criteria.UniqueResult<StoryVoteEntity>();
        }
    }
}