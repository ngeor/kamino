using System;
using Castle.DynamicProxy;

namespace BuzzStats.WebApi.Storage.Session
{
    public class DisposeInterceptor : IInterceptor
    {
        private readonly Action _disposeAction;

        public DisposeInterceptor(Action disposeAction)
        {
            _disposeAction = disposeAction;
        }

        public void Intercept(IInvocation invocation)
        {
            invocation.Proceed();

            if (invocation.Method.Name == "Dispose")
            {
                _disposeAction();
            }
        }

        public static TInterface Decorate<TInterface>(TInterface session, Action disposeAction)
            where TInterface : class
        {
            ProxyGenerator proxyGenerator = new ProxyGenerator();
            DisposeInterceptor interceptor = new DisposeInterceptor(disposeAction);
            return proxyGenerator.CreateInterfaceProxyWithTargetInterface(session, interceptor);
        }
    }
}
