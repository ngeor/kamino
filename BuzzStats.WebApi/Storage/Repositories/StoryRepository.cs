using BuzzStats.WebApi.Storage.Entities;
using NHibernate;
using NHibernate.Criterion;

namespace BuzzStats.WebApi.Storage.Repositories
{
    public class StoryRepository
    {
        public virtual StoryEntity GetByStoryId(ISession session, int storyId)
        {
            var criteria = session.CreateCriteria<StoryEntity>();
            criteria = criteria.Add(Restrictions.Eq("StoryId", storyId));
            return criteria.UniqueResult<StoryEntity>();
        }
    }
}