using BuzzStats.WebApi.DTOs;
using NHibernate;

namespace BuzzStats.WebApi.Storage
{
    public interface IUpdater
    {
        void Save(ISession session, Story story);
    }
}