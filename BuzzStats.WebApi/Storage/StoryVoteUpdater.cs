using System.Collections.Generic;
using System.Linq;
using BuzzStats.WebApi.DTOs;
using BuzzStats.WebApi.Storage.Entities;
using BuzzStats.WebApi.Storage.Repositories;
using log4net;
using NGSoftware.Common.Messaging;
using NHibernate;

namespace BuzzStats.WebApi.Storage
{
    public class StoryVoteUpdater : IStoryVoteUpdater
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof(StoryVoteUpdater));
        private readonly StoryMapper _storyMapper;
        private readonly StoryVoteRepository _storyVoteRepository;
        private readonly IMessageBus _messageBus;

        public StoryVoteUpdater(StoryMapper storyMapper, StoryVoteRepository storyVoteRepository, IMessageBus messageBus)
        {
            _storyMapper = storyMapper;
            _storyVoteRepository = storyVoteRepository;
            _messageBus = messageBus;
        }

        public void SaveStoryVotes(ISession session, Story story, StoryEntity storyEntity)
        {
            IList<StoryVoteEntity> existingStoryVotes = _storyVoteRepository.Get(session, storyEntity);
            IList<StoryVoteEntity> newStoryVotes = _storyMapper.ToStoryVoteEntities(story, storyEntity);
            foreach (var storyVoteEntity in newStoryVotes)
            {
                Log.DebugFormat("Saving vote {0} on story {1}", storyVoteEntity.Username, story.StoryId);
                if (existingStoryVotes.All(e => e.Username != storyVoteEntity.Username))
                {
                    session.Save(storyVoteEntity);
                    _messageBus.Publish(storyVoteEntity);
                    Log.InfoFormat("Saved new vote, db id {0}", storyVoteEntity.Id);
                }
                else
                {
                    Log.DebugFormat("Already existed");
                }
            }

            foreach (var existingStoryVote in existingStoryVotes)
            {
                Log.DebugFormat("Examining existing vote {0} on story {1}", existingStoryVote.Username, story.StoryId);
                if (newStoryVotes.All(e => e.Username != existingStoryVote.Username))
                {
                    session.Delete(existingStoryVote);
                    Log.WarnFormat("Vote disappeared, deleted");
                }
                else
                {
                    Log.DebugFormat("Vote still exists");
                }
            }
        }
    }
}