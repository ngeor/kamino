using System;
using System.Configuration;
using BuzzStats.WebApi.Storage.ClassMaps;
using FluentNHibernate.Cfg;
using FluentNHibernate.Cfg.Db;
using log4net;
using NGSoftware.Common.Configuration;
using NHibernate;
using NHibernate.Tool.hbm2ddl;
using Configuration = NHibernate.Cfg.Configuration;

namespace BuzzStats.WebApi.Storage
{
    class SessionFactoryFactory : ISessionFactoryFactory
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof(SessionFactoryFactory));
        private readonly IAppSettings _appSettings;

        public SessionFactoryFactory(IAppSettings appSettings)
        {
            _appSettings = appSettings;
        }
        
        public ISessionFactory Create()
        {
            Log.Info("Creating session factory");
            try
            {
                var sessionFactory = Fluently.Configure()
                    .Database(MySQLConfiguration.Standard.ConnectionString(ConnectionString()))
                    .Mappings(m => m.FluentMappings.AddFromAssemblyOf<StoryMap>())
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

        private void BuildSchema(Configuration cfg)
        {
            bool exportSchema = !string.IsNullOrWhiteSpace(_appSettings["ExportSchema"]);
            new SchemaExport(cfg).Create(true, exportSchema);
        }

        private string ConnectionString()
        {
            return ConnectionStringFromEnvironment() ?? ConnectionStringFromAppConfig();
        }

        private string ConnectionStringFromAppConfig()
        {
            return ConfigurationManager.ConnectionStrings["BuzzStats"].ConnectionString;
        }

        private string ConnectionStringFromEnvironment()
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