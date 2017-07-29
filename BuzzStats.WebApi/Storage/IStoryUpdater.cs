using BuzzStats.CrawlerService.DTOs;
using BuzzStats.StorageWebApi.Entities;
using NHibernate;

namespace BuzzStats.StorageWebApi
{
    public interface IStoryUpdater
    {
        StoryEntity Save(ISession session, Story story);
    }
}