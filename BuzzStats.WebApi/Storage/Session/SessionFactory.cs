using Castle.DynamicProxy;
using NGSoftware.Common.Factories;
using NHibernate;
using IInterceptor = Castle.DynamicProxy.IInterceptor;

namespace BuzzStats.WebApi.Storage.Session
{
    /// <summary>
    /// This class is able to create a new NHibernate session.
    /// </summary>
    public class SessionFactory : IFactory<ISession>
    {
        private readonly ISessionFactory _sessionFactory;

        public SessionFactory(ISessionFactory sessionFactory)
        {
            _sessionFactory = sessionFactory;
        }

        public ISession Create()
        {
            return _sessionFactory.OpenSession();
        }
    }

    /// <summary>
    /// This class creates NHibernate sessions using a proxy, so that they're lazily created.
    /// </summary>
    class ProxySessionFactory : IFactory<ISession>
    {
        private readonly ProxyGenerator _proxyGenerator = new ProxyGenerator();
        private readonly ISessionFactory _sessionFactory;

        public ProxySessionFactory(ISessionFactory sessionFactory)
        {
            _sessionFactory = sessionFactory;
        }

        public ISession Create()
        {
            return _proxyGenerator.CreateInterfaceProxyWithoutTarget<ISession>(
                new SessionInterceptor(_sessionFactory));
        }
    }

    /// <summary>
    /// A session interceptor which postpones creating the real session object.
    /// </summary>
    class SessionInterceptor : IInterceptor
    {
        private readonly ISessionFactory _sessionFactory;
        private ISession _session;

        public SessionInterceptor(ISessionFactory sessionFactory)
        {
            _sessionFactory = sessionFactory;
        }

        public void Intercept(IInvocation invocation)
        {
            if (_session == null)
            {
                _session = _sessionFactory.OpenSession();
            }

            invocation.ReturnValue = invocation.Method.Invoke(_session, invocation.Arguments);
        }
    }
}
