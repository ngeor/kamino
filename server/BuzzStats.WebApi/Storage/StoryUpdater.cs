using AutoMapper;
using BuzzStats.DTOs;
using BuzzStats.WebApi.Storage.Entities;
using BuzzStats.WebApi.Storage.Repositories;
using log4net;
using NGSoftware.Common.Messaging;
using NHibernate;

namespace BuzzStats.WebApi.Storage
{
    public class StoryUpdater : IStoryUpdater
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof(StoryUpdater));
        private readonly IMapper _mapper;
        private readonly IStoryRepository _storyRepository;
        private readonly IMessageBus _messageBus;

        public StoryUpdater(IMapper mapper, IStoryRepository storyRepository, IMessageBus messageBus)
        {
            _mapper = mapper;
            _storyRepository = storyRepository;
            _messageBus = messageBus;
        }

        public virtual StoryEntity Save(ISession session, Story story)
        {
            Log.InfoFormat("Saving story {0}", story.StoryId);
            var existingStoryEntity = _storyRepository.GetByStoryId(story.StoryId);

            if (existingStoryEntity == null)
            {
                var storyEntity = _mapper.Map<StoryEntity>(story);
                session.Save(storyEntity);
                Log.InfoFormat("Saved new story, db id: {0}", storyEntity.Id);
                session.Save(new RecentActivityEntity
                {
                    CreatedAt = storyEntity.CreatedAt,
                    Story = storyEntity
                });

                _messageBus.Publish(storyEntity);
                return storyEntity;
            }

            var updatedStoryEntity = _mapper.Map(story, existingStoryEntity);
            session.Update(updatedStoryEntity);
            Log.InfoFormat("Updated existing story, db id: {0}", updatedStoryEntity.Id);
            return updatedStoryEntity;
        }
    }
}
