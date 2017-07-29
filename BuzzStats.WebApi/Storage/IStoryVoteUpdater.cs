using BuzzStats.WebApi.DTOs;
using BuzzStats.WebApi.Storage.Entities;
using NHibernate;

namespace BuzzStats.WebApi.Storage
{
    public interface IStoryVoteUpdater
    {
        void SaveStoryVotes(ISession session, Story story, StoryEntity storyEntity);
    }
}