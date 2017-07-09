using BuzzStats.StorageWebApi.DTOs;
using BuzzStats.StorageWebApi.Repositories;
using NHibernate;

namespace BuzzStats.StorageWebApi
{
    public class Updater
    {
        private readonly StoryMapper _storyMapper;
        private readonly StoryRepository _storyRepository;

        public Updater(StoryMapper storyMapper, StoryRepository storyRepository)
        {
            _storyMapper = storyMapper;
            _storyRepository = storyRepository;
        }

        public virtual void Save(ISession session, Story story)
        {
            var storyEntity = _storyMapper.ToStoryEntity(story);
            var existingStoryEntity = _storyRepository.GetByStoryId(session, story.StoryId);

            if (existingStoryEntity == null)
            {
                session.Save(storyEntity);
            }
            else
            {
                session.SaveOrUpdate(storyEntity);
            }

            session.Flush();
        }
    }
}