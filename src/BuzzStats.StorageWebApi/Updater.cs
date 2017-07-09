using BuzzStats.StorageWebApi.DTOs;
using NHibernate;

namespace BuzzStats.StorageWebApi
{
    public class Updater
    {
        private readonly StoryMapper _storyMapper;

        public Updater(StoryMapper storyMapper)
        {
            _storyMapper = storyMapper;
        }

        public virtual void Save(ISession session, Story story)
        {
            var storyEntity = _storyMapper.ToStoryEntity(story);
            session.SaveOrUpdate(storyEntity);
            session.Flush();
        }
    }
}