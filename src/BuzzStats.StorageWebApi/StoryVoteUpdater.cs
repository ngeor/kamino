using System.Collections.Generic;
using System.Linq;
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
            IList<StoryVoteEntity> existingStoryVotes = _storyVoteRepository.Get(session, storyEntity);
            IList<StoryVoteEntity> newStoryVotes = _storyMapper.ToStoryVoteEntities(story, storyEntity);
            foreach (var storyVoteEntity in newStoryVotes)
            {
                if (existingStoryVotes.All(e => e.Username != storyVoteEntity.Username))
                {
                    session.SaveOrUpdate(storyVoteEntity);
                }
            }

            foreach (var existingStoryVote in existingStoryVotes)
            {
                if (newStoryVotes.All(e => e.Username != existingStoryVote.Username))
                {
                    session.Delete(existingStoryVote);
                }
            }
        }
    }
}