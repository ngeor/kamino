using BuzzStats.CrawlerService.DTOs;
using NHibernate;

namespace BuzzStats.StorageWebApi
{
    public interface IUpdater
    {
        void Save(ISession session, Story story);
    }
}