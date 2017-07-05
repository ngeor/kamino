// --------------------------------------------------------------------------------
// <copyright file="SessionFactoryBuilder.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/09/04
// * Time: 1:27 μμ
// --------------------------------------------------------------------------------

using System;
using System.Net.Sockets;
using System.Reflection;
using FluentNHibernate.Cfg;
using FluentNHibernate.Cfg.Db;
using NHibernate;
using NHibernate.Tool.hbm2ddl;
using NGSoftware.Common;
using NGSoftware.Common.Collections;
using BuzzStats.Data.NHibernate.ClassMaps;

namespace BuzzStats.Data.NHibernate
{
    static class SessionFactoryBuilder
    {
        internal static ISessionFactory Create(IPersistenceConfigurer persistenceConfigurer, bool createSchema)
        {
            FluentConfiguration x = Fluently.Configure()
                .Database(persistenceConfigurer)
                .Mappings(m => m.FluentMappings.AddFromAssemblyOf<StoryMap>());

            if (createSchema)
            {
                x = x.ExposeConfiguration(config => new SchemaExport(config).Create(false, true));
            }

#if __MonoCS__ // otherwise Mono doesn't work
            x = x.ExposeConfiguration(cfg => cfg.SetProperty("adonet.batch_size", "0"));

            #endif

            return BuildSessionFactory(x);
        }

        private static ISessionFactory BuildSessionFactory(FluentConfiguration x)
        {
            try
            {
                return x.BuildSessionFactory();
            }
            catch (FluentConfigurationException ex)
            {
                Exception transformedException = TransformException(ex);
                if (transformedException != null)
                {
                    throw transformedException;
                }

                throw;
            }
        }

        private static Exception TransformException(FluentConfigurationException ex)
        {
            return FindReflectionTypeLoadException(ex) ?? FindSocketException(ex);
        }

        private static Exception FindReflectionTypeLoadException(FluentConfigurationException ex)
        {
            ReflectionTypeLoadException inner = ex.GetReflectionTypeLoadException();
            if (inner != null)
            {
                string loaderExceptions = inner.LoaderExceptions.ToArrayString();
                return new InvalidOperationException("Error creating session factory: " + loaderExceptions, inner);
            }
            else
            {
                return null;
            }
        }

        private static Exception FindSocketException(Exception ex)
        {
            return ex is SocketException ? ex : (ex == null ? null : FindSocketException(ex.InnerException));
        }
    }
}