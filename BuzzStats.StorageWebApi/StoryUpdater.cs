using BuzzStats.StorageWebApi.DTOs;
using BuzzStats.StorageWebApi.Entities;
using BuzzStats.StorageWebApi.Repositories;
using NHibernate;

namespace BuzzStats.StorageWebApi
{
    public class StoryUpdater : IStoryUpdater
    {
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
            var existingStoryEntity = _storyRepository.GetByStoryId(session, story.StoryId);

            if (existingStoryEntity == null)
            {
                session.Save(storyEntity);
                return storyEntity;
            }
            
            // TODO Update changed fields
            existingStoryEntity.Title = story.Title;
            session.Update(existingStoryEntity);
            return existingStoryEntity;
        }
    }
}