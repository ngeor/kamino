using NHibernate;

namespace BuzzStats.WebApi.Storage.Session
{
    public interface ISessionManager
    {
        ISession Session { get; }
        ISession Create();
    }
}
