// --------------------------------------------------------------------------------
// <copyright file="StructureMapServiceLocator.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/12/18
// * Time: 21:05:09
// --------------------------------------------------------------------------------

using System;
using System.Collections.Generic;
using System.Linq;
using Microsoft.Practices.ServiceLocation;
using StructureMap;

namespace BuzzStats.Boot
{

    class StructureMapServiceLocator : ServiceLocatorImplBase
    {
        private readonly IContainer _container;
        public StructureMapServiceLocator(IContainer container)
        {
            _container = container;
        }

        protected override object DoGetInstance(Type serviceType, string key)
        {
            return key == null ? _container.GetInstance(serviceType) : _container.GetInstance(serviceType, key);
        }

        protected override IEnumerable<object> DoGetAllInstances(Type serviceType)
        {
            return _container.GetAllInstances(serviceType).OfType<object>();
        }
    }
}
