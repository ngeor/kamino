//
//  MySqlRecentActivityRepository.cs
//
//  Author:
//       ngeor
//
//  Copyright (c) 2014 ngeor

using System.Linq;
using NHibernate;
using NHibernate.Transform;
using BuzzStats.Data.NHibernate.Entities;

namespace BuzzStats.Data.NHibernate.MySql
{
    internal sealed class MySqlRecentActivityRepository : CoreDataClient, IRecentActivityRepository
    {
        internal MySqlRecentActivityRepository(ISession session) : base(session)
        {
        }

        #region IRecentActivityRepository implementation

        public RecentActivity[] Get(RecentActivityRequest request)
        {
            ISQLQuery q;
            if (request.Username != null)
            {
                q =
                    Session.CreateSQLQuery(
                        "SELECT * FROM RecentActivity WHERE Username = ? ORDER BY CreatedAt DESC LIMIT ?");
                q.SetParameter(0, request.Username);
                q.SetParameter(1, request.MaxCount);
            }
            else
            {
                q = Session.CreateSQLQuery("SELECT * FROM RecentActivity ORDER BY CreatedAt DESC LIMIT ?");
                q.SetParameter(0, request.MaxCount);
            }

            var result = q
                .AddScalar("StoryId", NHibernateUtil.Int32)
                .AddScalar("CommentId", NHibernateUtil.Int32)
                .AddScalar("Title", NHibernateUtil.String)
                .AddScalar("Username", NHibernateUtil.String)
                .AddScalar("CreatedAt", NHibernateUtil.DateTime)
                .AddScalar("DetectedAt", NHibernateUtil.DateTime)
                .AddScalar("What", NHibernateUtil.Int32)
                .SetResultTransformer(Transformers.AliasToBean<RecentActivityEntity>())
                .List<RecentActivityEntity>();

            return result.Select(e => e.ToData()).ToArray();
        }

        #endregion
    }
}