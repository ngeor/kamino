using BuzzStats.StorageWebApi.DTOs;
using BuzzStats.StorageWebApi.Entities;
using NHibernate;

namespace BuzzStats.StorageWebApi
{
    public interface ICommentUpdater
    {
        void SaveComments(ISession session, Story story, StoryEntity storyEntity);
    }
}