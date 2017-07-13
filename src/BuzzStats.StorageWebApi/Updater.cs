using BuzzStats.StorageWebApi.DTOs;
using BuzzStats.StorageWebApi.Entities;
using NHibernate;

namespace BuzzStats.StorageWebApi
{
    public class Updater
    {
        private readonly IStoryUpdater _storyUpdater;
        private readonly IStoryVoteUpdater _storyVoteUpdater;

        public Updater(IStoryUpdater storyUpdater, IStoryVoteUpdater storyVoteUpdater)
        {
            _storyUpdater = storyUpdater;
            _storyVoteUpdater = storyVoteUpdater;
        }

        public virtual void Save(ISession session, Story story)
        {
            var storyEntity = _storyUpdater.Save(session, story);
            _storyVoteUpdater.SaveStoryVotes(session, story, storyEntity);
            session.Flush();
        }
    }
}