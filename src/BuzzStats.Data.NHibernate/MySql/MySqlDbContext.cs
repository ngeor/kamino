// --------------------------------------------------------------------------------
// <copyright file="MySqlDbContext.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/10/17
// * Time: 14:34:15
// --------------------------------------------------------------------------------

using NHibernate;

namespace BuzzStats.Data.NHibernate.MySql
{
    public class MySqlDbContext : DbContext
    {
        public MySqlDbContext(ISessionFactory sessionFactory)
            : base(sessionFactory)
        {
        }

        protected override IDbSession CreateSafe(ISession session)
        {
            return new MySqlDbSession(session);
        }
    }
}
