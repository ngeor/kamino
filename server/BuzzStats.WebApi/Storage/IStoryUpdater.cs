using BuzzStats.DTOs;
using BuzzStats.WebApi.Storage.Entities;
using NHibernate;

namespace BuzzStats.WebApi.Storage
{
    public interface IStoryUpdater
    {
        StoryEntity Save(ISession session, Story story);
    }
}