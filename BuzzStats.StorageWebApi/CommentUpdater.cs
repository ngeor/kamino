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
            CommentEntityHolder[] commentEntities = _storyMapper.ToCommentEntities(story, storyEntity);
            foreach (var commentEntity in commentEntities)
            {
                var existingComment = _commentRepository.GetByCommentId(session, commentEntity.Entity.CommentId);
                if (existingComment == null)
                {
                    session.Save(commentEntity.Entity);
                }
                else
                {
                    // TODO update if there are vote differences
                    // and/or raise event for other service
                    // and/or add event in db instead of updating records
                    commentEntity.Entity = existingComment; // this updates child comments
                }
            }
        }
    }
}