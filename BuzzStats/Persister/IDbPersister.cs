using BuzzStats.Data;

namespace BuzzStats.Persister
{
    public interface IDbPersister : IPersister
    {
        IDbSession DbSession { get; set; }
    }
}