using System;
using System.Collections.Generic;
using System.Linq;
using Microsoft.Practices.ServiceLocation;
using StructureMap;

namespace BuzzStats.Boot
{
    /// <summary>
    /// Holds the global container of the application.
    /// </summary>
    public static class ObjectFactory
    {
        private static readonly object _mutex = new object();
        private static bool _isInitialized = false;

        /// <summary>
        /// Initialize the application container.
        /// </summary>
        /// <param name="x">The configuration expression.</param>
        public static void Initialize(Action<ConfigurationExpression> x)
        {
            if (_isInitialized)
            {
                return;
            }

            lock (_mutex)
            {
                if (_isInitialized)
                {
                    return;
                }

                var container = new Container();
                container.Configure(_ => _.For<IContainer>().Use(container));
                container.Configure(x);

                var locator = new StructureMapServiceLocator(container);
                ServiceLocator.SetLocatorProvider(() => locator);
                _isInitialized = true;
            }
        }

        public static void ShutDown()
        {
            _isInitialized = false;
            ServiceLocator.SetLocatorProvider(null);
        }
    }
}
