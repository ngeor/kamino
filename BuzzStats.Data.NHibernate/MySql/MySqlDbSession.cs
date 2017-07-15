// --------------------------------------------------------------------------------
// <copyright file="MySqlDbSession.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/10/17
// * Time: 14:21:11
// --------------------------------------------------------------------------------

using NHibernate;

namespace BuzzStats.Data.NHibernate.MySql
{
    public class MySqlDbSession : DbSession
    {
        private IRecentActivityRepository _recentActivityRepository;

        public MySqlDbSession(ISession session) : base(session)
        {
        }

        public override IRecentActivityRepository RecentActivityRepository
        {
            get
            {
                return InitializeDataLayer(
                    ref _recentActivityRepository,
                    () => new MySqlRecentActivityRepository(Session));
            }
        }
    }
}