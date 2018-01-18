using Castle.DynamicProxy;
using NGSoftware.Common;
using NHibernate;
using IInterceptor = Castle.DynamicProxy.IInterceptor;

namespace BuzzStats.WebApi.Storage.Session
{
    public class RepositoryInterceptor : IInterceptor
    {
        private readonly ISessionManager _sessionManager;

        public RepositoryInterceptor(ISessionManager sessionManager)
        {
            _sessionManager = sessionManager;
        }

        public void Intercept(IInvocation invocation)
        {
            bool hasSession = _sessionManager.Session != null;
            ISession session = null;
            if (!hasSession)
            {
                session = _sessionManager.Create();
            }

            try
            {
                invocation.Proceed();
            }
            finally
            {
                session.SafeDispose();
            }
        }

        public static TInterface Decorate<TInterface>(TInterface realInterface, ISessionManager sessionManager)
            where TInterface : class
        {
            ProxyGenerator proxyGenerator = new ProxyGenerator();
            return proxyGenerator.CreateInterfaceProxyWithTargetInterface(
                realInterface,
                new RepositoryInterceptor(sessionManager));
        }
    }
}
