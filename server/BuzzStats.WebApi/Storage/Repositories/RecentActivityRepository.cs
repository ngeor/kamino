using System.Collections.Generic;
using BuzzStats.WebApi.Storage.Entities;
using NHibernate;
using NHibernate.Criterion;

namespace BuzzStats.WebApi.Storage.Repositories
{
    /// <summary>
    /// Repository for <see cref="RecentActivityEntity"/>.
    /// </summary>
    public class RecentActivityRepository : IRecentActivityRepository
    {
        private readonly ISession _session;

        public RecentActivityRepository(ISession session)
        {
            _session = session;
        }

        public virtual IList<RecentActivityEntity> Get()
        {
            var criteria = _session.CreateCriteria<RecentActivityEntity>();
            criteria = criteria.AddOrder(Order.Desc("CreatedAt"));
            criteria = criteria.AddOrder(Order.Desc("Id"));
            criteria = criteria.SetMaxResults(20);
            return criteria.List<RecentActivityEntity>();
        }
    }
}
