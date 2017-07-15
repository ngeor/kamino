using BuzzStats.StorageWebApi.DTOs;
using BuzzStats.StorageWebApi.Entities;
using BuzzStats.StorageWebApi.Repositories;
using NHibernate;

namespace BuzzStats.StorageWebApi
{
    public class CommentUpdater : ICommentUpdater
    {
        private readonly StoryMapper _storyMapper;
        private readonly CommentRepository _commentRepository;

        public CommentUpdater(StoryMapper storyMapper, CommentRepository commentRepository)
        {
            _storyMapper = storyMapper;
            _commentRepository = commentRepository;
        }

        public void SaveComments(ISession session, Story story, StoryEntity storyEntity)
        {
            var commentEntities = _storyMapper.ToCommentEntities(story, storyEntity);
            foreach (var commentEntity in commentEntities)
            {
                session.SaveOrUpdate(commentEntity);
            }
        }
    }
}