using System.Collections.Generic;
using BuzzStats.StorageWebApi.Entities;
using NHibernate;
using NHibernate.Criterion;

namespace BuzzStats.StorageWebApi.Repositories
{
    public class StoryVoteRepository
    {
        public virtual IList<StoryVoteEntity> Get(ISession session, StoryEntity story)
        {
            var criteria = session.CreateCriteria<StoryVoteEntity>();
            criteria = criteria.Add(Restrictions.Eq("Story", story));
            return criteria.List<StoryVoteEntity>();
        }
    }
}