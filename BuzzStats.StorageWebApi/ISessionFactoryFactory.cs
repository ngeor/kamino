using NHibernate;

namespace BuzzStats.StorageWebApi
{
    internal interface ISessionFactoryFactory
    {
        ISessionFactory Create();
    }
}