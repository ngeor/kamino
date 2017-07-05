using System;
using System.Data.Common;
using System.Reflection;
using log4net;
using NHibernate;
using NGSoftware.Common;

namespace BuzzStats.Data.NHibernate
{
    public class DbContext : IDbContext
    {
        private static readonly ILog Log = LogManager.GetLogger(
            MethodBase.GetCurrentMethod().DeclaringType);

        public DbContext(ISessionFactory sessionFactory)
        {
            if (sessionFactory == null)
            {
                throw new ArgumentNullException("sessionFactory");
            }

            Log.Debug("constructor");
            SessionFactory = sessionFactory;
        }

        private ISessionFactory SessionFactory { get; set; }

        public IDbSession OpenSession()
        {
            if (SessionFactory == null)
            {
                throw new ObjectDisposedException(
                    "DbContext",
                    "Opening session is not possible because this DbContext has been disposed");
            }

            Log.Debug("OpenSession");
            return CreateSafe(SessionFactory.OpenSession());
        }

        internal IDbSession OpenSession(DbConnection connection)
        {
            if (connection == null)
            {
                throw new ArgumentNullException("connection");
            }

            if (SessionFactory == null)
            {
                throw new ObjectDisposedException(
                    "DbContext",
                    "Opening session is not possible because this DbContext has been disposed");
            }

            Log.Debug("OpenSession(connection)");
            return CreateSafe(SessionFactory.OpenSession(connection));
        }

        public void Dispose()
        {
            Log.Debug("Dispose");
            SessionFactory.SafeDispose();
            SessionFactory = null;
        }

        protected virtual IDbSession CreateSafe(ISession session)
        {
            return new DbSession(session);
        }
    }
}