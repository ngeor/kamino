using BuzzStats.StorageWebApi.DTOs;
using BuzzStats.StorageWebApi.Entities;
using NHibernate;

namespace BuzzStats.StorageWebApi
{
    public class Updater
    {
        private readonly StoryMapper _storyMapper;
        private readonly IStoryUpdater _storyUpdater;

        public Updater(StoryMapper storyMapper, IStoryUpdater storyUpdater)
        {
            _storyMapper = storyMapper;
            _storyUpdater = storyUpdater;
        }

        public virtual void Save(ISession session, Story story)
        {
            var storyEntity = _storyUpdater.Save(session, story);

            SaveStoryVotes(session, story, storyEntity);

            session.Flush();
        }

        private void SaveStoryVotes(ISession session, Story story, StoryEntity storyEntity)
        {
            foreach (var storyVoteEntity in _storyMapper.ToStoryVoteEntities(story, storyEntity))
            {
                session.Save(storyVoteEntity);
            }
        }
    }
}