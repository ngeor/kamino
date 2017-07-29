using System;
using System.Collections.Generic;
using System.Linq;
using System.Web.Http.Dependencies;
using log4net;
using StructureMap;

namespace BuzzStats.WebApi.Storage
{
    sealed class StructureMapDependencyResolver : IDependencyResolver
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof(StructureMapDependencyResolver));
        private readonly IContainer _container;

        public StructureMapDependencyResolver(IContainer container)
        {
            _container = container;
        }

        public void Dispose()
        {
            _container.Dispose();
        }

        public object GetService(Type serviceType)
        {
            try
            {
                if (serviceType.IsAbstract || serviceType.IsInterface)
                {
                    return _container.TryGetInstance(serviceType);
                }
                else
                {
                    return _container.GetInstance(serviceType);
                }
            }
            catch (Exception ex)
            {
                Log.Error(ex.Message, ex);
                throw;
            }
        }

        public IEnumerable<object> GetServices(Type serviceType)
        {
            return _container.GetAllInstances<object>().Where(s => s.GetType() == serviceType);
        }

        public IDependencyScope BeginScope()
        {
            return new StructureMapDependencyResolver(_container.GetNestedContainer());
        }
    }
}