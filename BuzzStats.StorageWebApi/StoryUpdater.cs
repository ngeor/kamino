using AutoMapper;
using BuzzStats.StorageWebApi.DTOs;
using BuzzStats.StorageWebApi.Entities;
using BuzzStats.StorageWebApi.Repositories;
using log4net;
using NHibernate;

namespace BuzzStats.StorageWebApi
{
    public class StoryUpdater : IStoryUpdater
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof(StoryUpdater));
        private readonly IMapper _mapper;
        private readonly StoryRepository _storyRepository;

        public StoryUpdater(IMapper mapper, StoryRepository storyRepository)
        {
            _mapper = mapper;
            _storyRepository = storyRepository;
        }
        
        public virtual StoryEntity Save(ISession session, Story story)
        {
            Log.InfoFormat("Saving story {0}", story.StoryId);
            var existingStoryEntity = _storyRepository.GetByStoryId(session, story.StoryId);

            if (existingStoryEntity == null)
            {
                var storyEntity = _mapper.Map<StoryEntity>(story);
                session.Save(storyEntity);
                Log.InfoFormat("Saved new story, db id: {0}", storyEntity.Id);
                return storyEntity;
            }

            var updatedStoryEntity = _mapper.Map(story, existingStoryEntity);
            session.Update(updatedStoryEntity);
            Log.InfoFormat("Updated existing story, db id: {0}", updatedStoryEntity.Id);
            return updatedStoryEntity;
        }
    }
}