using System.Collections.Generic;
using BuzzStats.WebApi.Storage.Entities;
using NHibernate;
using NHibernate.Criterion;

namespace BuzzStats.WebApi.Storage.Repositories
{
    /// <summary>
    /// Repository for <see cref="RecentActivityEntity"/>.
    /// </summary>
    public class RecentActivityRepository
    {
        public virtual IList<RecentActivityEntity> Get(ISession session)
        {
            var criteria = session.CreateCriteria<RecentActivityEntity>();
            criteria = criteria.AddOrder(Order.Desc("CreatedAt"));
            criteria = criteria.AddOrder(Order.Desc("Id"));
            criteria = criteria.SetMaxResults(20);
            return criteria.List<RecentActivityEntity>();
        }
    }
}
