// --------------------------------------------------------------------------------
// <copyright file="SqliteSessionFactoryBuilder.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/09/14
// * Time: 9:36 μμ
// --------------------------------------------------------------------------------

using System;
using System.Configuration;
using System.IO;
using System.Reflection;
using System.Web;
using FluentNHibernate.Cfg.Db;
using BuzzStats.Data.NHibernate.SessionImpl;

namespace BuzzStats.Data.NHibernate
{
    internal class SqliteSessionFactoryBuilder : IPersistenceConfigurerBuilder
    {
        public IPersistenceConfigurer Create(ConnectionStringSettings connectionParameters)
        {
            string file = ResolveFilename(connectionParameters.ConnectionString);

            SQLiteConfiguration configuration;
            if (Type.GetType("Mono.Runtime") != null)
            {
                // we're running Mono
                Assembly.Load("Mono.Data.Sqlite, Version=4.0.0.0, Culture=neutral, PublicKeyToken=0738eb9f132ed756");

                configuration = SQLiteConfiguration.Standard.Driver<MonoSqliteDriver>().UsingFile(file);
            }
            else
            {
                configuration = SQLiteConfiguration.Standard.UsingFile(file);
            }

            if (connectionParameters.ShouldShowSql())
            {
                configuration = configuration.ShowSql();
            }

            return configuration;
        }

        private static string NormalizeDirectorySeparator(string path)
        {
            return path.Replace('/', Path.DirectorySeparatorChar).Replace('\\', Path.DirectorySeparatorChar);
        }

        private string ResolveFilename(string connectionString)
        {
            connectionString = connectionString.Replace("Data Source=", "").Trim();
            if (connectionString.StartsWith("~/"))
            {
                // chop off the first two characters
                string tmp = NormalizeDirectorySeparator(connectionString.Substring(2));
                return Path.Combine(HttpRuntime.AppDomainAppPath, tmp);
            }

            return NormalizeDirectorySeparator(connectionString);
        }
    }
}