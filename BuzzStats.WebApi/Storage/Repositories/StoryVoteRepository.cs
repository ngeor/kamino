using System.Collections.Generic;
using BuzzStats.WebApi.Storage.Entities;
using NHibernate;
using NHibernate.Criterion;

namespace BuzzStats.WebApi.Storage.Repositories
{
    public class StoryVoteRepository : IStoryVoteRepository
    {
        private readonly ISession _session;

        public StoryVoteRepository(ISession session)
        {
            _session = session;
        }

        public virtual IList<StoryVoteEntity> Get(StoryEntity story)
        {
            var criteria = _session.CreateCriteria<StoryVoteEntity>();
            criteria = criteria.Add(Restrictions.Eq("Story", story));
            return criteria.List<StoryVoteEntity>();
        }
    }
}
