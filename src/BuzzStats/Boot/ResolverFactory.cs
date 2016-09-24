// --------------------------------------------------------------------------------
// <copyright file="ResolverFactory.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/12/18
// * Time: 21:46:40
// --------------------------------------------------------------------------------
using System;
using Microsoft.Practices.ServiceLocation;
using NGSoftware.Common.Factories;

namespace BuzzStats.Boot
{
    public class ResolverFactory<T> : IFactory<T>
        where T : class
    {
        private readonly string _name;

        public ResolverFactory(string name)
        {
            _name = name;
        }

        public T Create()
        {
            return _name != null ? ServiceLocator.Current.GetInstance<T>(_name) : ServiceLocator.Current.GetInstance<T>();
        }
    }
}
