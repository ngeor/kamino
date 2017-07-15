using System;
using FluentNHibernate.Cfg;
using FluentNHibernate.Cfg.Db;
using log4net;
using NHibernate;
using NHibernate.Cfg;
using NHibernate.Tool.hbm2ddl;

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
                    .Database(MySQLConfiguration.Standard.ConnectionString(c =>
                        c.FromConnectionStringWithKey("BuzzStats")))
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
    }
}