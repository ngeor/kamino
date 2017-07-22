using System;
using System.Configuration;
using FluentNHibernate.Cfg;
using FluentNHibernate.Cfg.Db;
using log4net;
using NHibernate;
using NHibernate.Tool.hbm2ddl;
using Configuration = NHibernate.Cfg.Configuration;

namespace BuzzStats.StorageWebApi
{
    static class SessionFactoryFactory
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof(SessionFactoryFactory));
        
        internal static ISessionFactory Create()
        {
            Log.Info("Creating session factory");
            try
            {
                var sessionFactory = Fluently.Configure()
                    .Database(MySQLConfiguration.Standard.ConnectionString(ConnectionString()))
                    .Mappings(m => m.FluentMappings.AddFromAssemblyOf<StoryController>())
                    .ExposeConfiguration(BuildSchema)
                    .BuildSessionFactory();
                return sessionFactory;
            }
            catch (Exception ex)
            {
                Log.Error(ex.Message, ex);
                throw;
            }
        }

        private static void BuildSchema(Configuration cfg)
        {
            new SchemaExport(cfg).Create(true, true);
        }

        private static string ConnectionString()
        {
            return ConnectionStringFromEnvironment() ?? ConnectionStringFromAppConfig();
        }

        private static string ConnectionStringFromAppConfig()
        {
            return ConfigurationManager.ConnectionStrings["BuzzStats"].ConnectionString;
        }

        private static string ConnectionStringFromEnvironment()
        {
            string server = Environment.GetEnvironmentVariable("DB_SERVER");
            string database = Environment.GetEnvironmentVariable("DB_DATABASE");
            string user = Environment.GetEnvironmentVariable("DB_USER");
            string password = Environment.GetEnvironmentVariable("DB_PASSWORD");
            if (string.IsNullOrEmpty(server) || string.IsNullOrEmpty(database)
                || string.IsNullOrEmpty(user) || string.IsNullOrEmpty(password))
            {
                return null;
            }

//            return $"Server={server};Database={database};User Id={user};Password={password};";
            return $"Server={server};Database={database};Uid={user};Pwd={password};Charset=utf8";
        }
    }
}