using BuzzStats.WebApi.DTOs;
using BuzzStats.WebApi.Storage.Entities;
using NHibernate;

namespace BuzzStats.WebApi.Storage
{
    public interface ICommentUpdater
    {
        void SaveComments(ISession session, Story story, StoryEntity storyEntity);
    }
}