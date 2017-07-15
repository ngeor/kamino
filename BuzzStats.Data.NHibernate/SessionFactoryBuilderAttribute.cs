// --------------------------------------------------------------------------------
// <copyright file="SessionFactoryBuilderAttribute.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/09/14
// * Time: 9:41 μμ
// --------------------------------------------------------------------------------

using System;

namespace BuzzStats.Data.NHibernate
{
    [AttributeUsage(AttributeTargets.Field, Inherited = false, AllowMultiple = false)]
    internal sealed class SessionFactoryBuilderAttribute : Attribute
    {
        public SessionFactoryBuilderAttribute(Type sessionFactoryBuilderType)
        {
            SessionFactoryBuilderType = sessionFactoryBuilderType;
        }

        public Type SessionFactoryBuilderType { get; private set; }
    }
}