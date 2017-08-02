using System;
using NHibernate;

namespace BuzzStats.WebApi.Storage.Session
{
    /// <summary>
    /// This class is able to create a new NHibernate session.
    /// </summary>
    public class SessionManager : ISessionManager
    {
        private readonly ISessionFactory _sessionFactory;
        private ISession _session;

        public SessionManager(ISessionFactory sessionFactory)
        {
            _sessionFactory = sessionFactory;
        }

        public ISession Create()
        {
            if (_session != null)
            {
                throw new InvalidOperationException("Session already exists");
            }

            _session = DisposeInterceptor.Decorate(_sessionFactory.OpenSession(), Clear);
            return _session;
        }

        public ISession Session => _session;

        private void Clear()
        {
            _session = null;
        }
    }
}
