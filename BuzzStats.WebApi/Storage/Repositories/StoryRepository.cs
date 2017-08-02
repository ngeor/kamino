using BuzzStats.WebApi.Storage.Entities;
using NHibernate;
using NHibernate.Criterion;

namespace BuzzStats.WebApi.Storage.Repositories
{
    public class StoryRepository : IStoryRepository
    {
        private readonly ISession _session;

        public StoryRepository(ISession session)
        {
            _session = session;
        }

        public virtual StoryEntity GetByStoryId(int storyId)
        {
            var criteria = _session.CreateCriteria<StoryEntity>();
            criteria = criteria.Add(Restrictions.Eq("StoryId", storyId));
            return criteria.UniqueResult<StoryEntity>();
        }
    }
}
