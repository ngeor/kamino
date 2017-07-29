using NHibernate;

namespace BuzzStats.WebApi.Storage
{
    internal interface ISessionFactoryFactory
    {
        ISessionFactory Create();
    }
}