using BuzzStats.CrawlerService.DTOs;
using BuzzStats.StorageWebApi.Entities;
using NHibernate;

namespace BuzzStats.StorageWebApi
{
    public interface IStoryVoteUpdater
    {
        void SaveStoryVotes(ISession session, Story story, StoryEntity storyEntity);
    }
}