//
//  DbContextFactory.cs
//
//  Author:
//       ngeor
//
//  Copyright (c) 2014 ngeor

using System;
using System.Configuration;
using System.Reflection;
using FluentNHibernate.Cfg.Db;
using log4net;
using NHibernate;
using NGSoftware.Common.Factories;
using BuzzStats.Data.NHibernate.MySql;

namespace BuzzStats.Data.NHibernate
{
    public class DbContextFactory : IFactory<IDbContext>
    {
        private static readonly ILog Log = LogManager.GetLogger(
            MethodBase.GetCurrentMethod().DeclaringType);

        private readonly ConnectionStringSettings _connectionString;

        public DbContextFactory(ConnectionStringSettings connectionString)
        {
            if (connectionString == null)
            {
                throw new ArgumentNullException("connectionString");
            }

            _connectionString = connectionString;
        }

        public static IDbContext Create(ConnectionStringSettings connectionString)
        {
            return new DbContextFactory(connectionString).Create();
        }

        public IDbContext Create()
        {
            KnownDatabaseProvider knownDatabaseProvider = _connectionString.GetKnownDatabaseProvider();
            IPersistenceConfigurerBuilder pcb = SelectPersistenceConfigurerBuilder(knownDatabaseProvider);
            IPersistenceConfigurer pc = pcb.Create(_connectionString);
            ISessionFactory sf = SessionFactoryBuilder.Create(pc, _connectionString.ShouldCreateDb());
            switch (knownDatabaseProvider)
            {
                case KnownDatabaseProvider.MySql:
                    return new MySqlDbContext(sf);
                default:
                    return new DbContext(sf);
            }
        }

        private IPersistenceConfigurerBuilder SelectPersistenceConfigurerBuilder(
            KnownDatabaseProvider knownDatabaseProvider)
        {
            switch (knownDatabaseProvider)
            {
                case KnownDatabaseProvider.MySql:
                    return new MySqlSessionFactoryBuilder();

                case KnownDatabaseProvider.SQLite:
                    return new SqliteSessionFactoryBuilder();

                case KnownDatabaseProvider.MSSQL:
                    return new MsSqlSessionFactoryBuilder();
            }

            throw new NotSupportedException("Not supported provider: " + knownDatabaseProvider);
        }
    }
}