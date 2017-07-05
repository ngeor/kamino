using NHibernate;

namespace BuzzStats.Data.NHibernate
{
    public abstract class SessionClient
    {
        protected SessionClient(ISession session)
        {
            Session = session;
        }

        protected internal ISession Session { get; private set; }
    }
}