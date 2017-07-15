using System.Configuration;
using FluentNHibernate.Cfg.Db;

namespace BuzzStats.Data.NHibernate.MySql
{
    internal class MySqlSessionFactoryBuilder : IPersistenceConfigurerBuilder
    {
        public IPersistenceConfigurer Create(ConnectionStringSettings connectionParameters)
        {
            var persistenceConfigurer = MySQLConfiguration.Standard;
            if (connectionParameters.ShouldShowSql())
            {
                persistenceConfigurer = persistenceConfigurer.ShowSql();
            }

            return persistenceConfigurer.ConnectionString(connectionParameters.ConnectionString);
        }
    }
}