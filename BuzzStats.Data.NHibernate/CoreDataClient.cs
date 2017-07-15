using NHibernate;

namespace BuzzStats.Data.NHibernate
{
    public abstract class CoreDataClient : SessionClient
    {
        protected CoreDataClient(ISession session) : base(session)
        {
            CoreData = new CoreDataLayer(session);
        }

        protected internal CoreDataLayer CoreData { get; private set; }
    }
}