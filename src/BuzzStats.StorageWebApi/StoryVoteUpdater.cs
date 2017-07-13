using BuzzStats.StorageWebApi.DTOs;
using BuzzStats.StorageWebApi.Entities;
using BuzzStats.StorageWebApi.Repositories;
using NHibernate;

namespace BuzzStats.StorageWebApi
{
    public class StoryVoteUpdater : IStoryVoteUpdater
    {
        private readonly StoryMapper _storyMapper;
        private readonly StoryVoteRepository _storyVoteRepository;

        public StoryVoteUpdater(StoryMapper storyMapper, StoryVoteRepository storyVoteRepository)
        {
            _storyMapper = storyMapper;
            _storyVoteRepository = storyVoteRepository;
        }

        public void SaveStoryVotes(ISession session, Story story, StoryEntity storyEntity)
        {
            foreach (var storyVoteEntity in _storyMapper.ToStoryVoteEntities(story, storyEntity))
            {
                session.SaveOrUpdate(storyVoteEntity);
            }
        }
    }
}