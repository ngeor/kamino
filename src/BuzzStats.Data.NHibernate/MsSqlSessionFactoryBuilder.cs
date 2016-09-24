// --------------------------------------------------------------------------------
// <copyright file="MsSqlSessionFactoryBuilder.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/09/14
// * Time: 9:36 μμ
// --------------------------------------------------------------------------------

using System.Configuration;
using FluentNHibernate.Cfg.Db;
using BuzzStats.Data.NHibernate.SessionImpl;

namespace BuzzStats.Data.NHibernate
{
    internal class MsSqlSessionFactoryBuilder : IPersistenceConfigurerBuilder
    {
        public IPersistenceConfigurer Create(ConnectionStringSettings connectionParameters)
        {
            var persistenceConfigurer = MsSqlConfiguration.MsSql2008;
            if (connectionParameters.ShouldShowSql())
            {
                persistenceConfigurer = persistenceConfigurer.ShowSql();
            }

            persistenceConfigurer = persistenceConfigurer
                .Driver<ProfiledSql2008ClientDriver>()
                .ConnectionString(connectionParameters.ConnectionString);

            return persistenceConfigurer;
        }
    }
}
