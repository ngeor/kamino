// --------------------------------------------------------------------------------
// <copyright file="ConnectionStringsSettingsExtensions.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/10/17
// * Time: 13:42:59
// --------------------------------------------------------------------------------

using System;
using System.Configuration;
using System.Data.Common;
using NGSoftware.Common;

namespace BuzzStats.Data
{
    public static class ConnectionStringSettingsExtensions
    {
        public static bool ShouldCreateDb(this ConnectionStringSettings connectionString)
        {
            return Convert.ToBoolean(ConfigurationManager.AppSettings[connectionString.Name + ".CreateDb"]);
        }

        public static bool ShouldShowSql(this ConnectionStringSettings connectionString)
        {
            return Convert.ToBoolean(ConfigurationManager.AppSettings[connectionString.Name + ".ShowSql"]);
        }

        public static KnownDatabaseProvider GetKnownDatabaseProvider(this ConnectionStringSettings connectionString)
        {
            string providerName = connectionString.ProviderName;
            if (string.IsNullOrWhiteSpace(providerName))
            {
                throw new NotSupportedException("Missing provider name");
            }

            foreach (KnownDatabaseProvider kdb in Enum.GetValues(typeof(KnownDatabaseProvider)))
            {
                if (providerName.IndexOf(kdb.ToString(), StringComparison.OrdinalIgnoreCase) >= 0)
                {
                    return kdb;
                }
            }

            throw new NotSupportedException("Unsupported database provider: " + providerName);
        }

        public static DbConnection CreateConnection(this ConnectionStringSettings connectionString)
        {
            DbProviderFactory dbProviderFactory = DbProviderFactories.GetFactory(connectionString.ProviderName);
            DbConnection connection = null;
            try
            {
                connection = dbProviderFactory.CreateConnection();
                connection.ConnectionString = connectionString.ConnectionString;
                return connection;
            }
            catch
            {
                connection.SafeDispose();
                throw;
            }
        }

        public static DbConnection OpenConnection(this ConnectionStringSettings connectionString)
        {
            DbConnection connection = connectionString.CreateConnection();
            try
            {
                connection.Open();
                return connection;
            }
            catch
            {
                connection.SafeDispose();
                throw;
            }
        }

        public static object ExecuteScalar(this ConnectionStringSettings connectionString, string sql)
        {
            using (DbConnection connection = connectionString.OpenConnection())
            {
                return connection.ExecuteScalar(sql);
            }
        }
    }
}