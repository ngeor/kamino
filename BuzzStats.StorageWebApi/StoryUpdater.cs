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
        private readonly StoryMapper _storyMapper;
        private readonly StoryRepository _storyRepository;

        public StoryUpdater(StoryMapper storyMapper, StoryRepository storyRepository)
        {
            _storyMapper = storyMapper;
            _storyRepository = storyRepository;
        }
        
        public virtual StoryEntity Save(ISession session, Story story)
        {
            var storyEntity = _storyMapper.ToStoryEntity(story);
            Log.InfoFormat("Saving story {0}", story.StoryId);
            var existingStoryEntity = _storyRepository.GetByStoryId(session, story.StoryId);

            if (existingStoryEntity == null)
            {
                session.Save(storyEntity);
                Log.InfoFormat("Saved new story, db id: {0}", storyEntity.Id);
                return storyEntity;
            }

            var updatedStoryEntity = _storyMapper.UpdateStoryEntity(existingStoryEntity, storyEntity);
            session.Update(updatedStoryEntity);
            Log.InfoFormat("Updated existing story, db id: {0}", updatedStoryEntity.Id);
            return updatedStoryEntity;
        }
    }
}