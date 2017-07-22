using BuzzStats.StorageWebApi.DTOs;
using BuzzStats.StorageWebApi.Entities;
using BuzzStats.StorageWebApi.Repositories;
using log4net;
using NHibernate;

namespace BuzzStats.StorageWebApi
{
    public class CommentUpdater : ICommentUpdater
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof(CommentUpdater));
        private readonly StoryMapper _storyMapper;
        private readonly CommentRepository _commentRepository;

        public CommentUpdater(StoryMapper storyMapper, CommentRepository commentRepository)
        {
            _storyMapper = storyMapper;
            _commentRepository = commentRepository;
        }

        public void SaveComments(ISession session, Story story, StoryEntity storyEntity)
        {
            Log.InfoFormat("SaveComments of story {0}", story.StoryId);
            SaveComments(session, storyEntity, story.Comments, null);
        }

        private void SaveComments(ISession session, StoryEntity storyEntity, Comment[] comments, CommentEntity parentCommentEntity)
        {
            int? parentCommentId = parentCommentEntity?.CommentId;
            if (comments == null)
            {
                Log.InfoFormat("No comments for story id {0} comment id {1}", storyEntity.StoryId, parentCommentId);
                return;
            }

            foreach (var comment in comments)
            {
                CommentEntity commentEntity = SaveComment(session, storyEntity, comment, parentCommentEntity);
                SaveComments(session, storyEntity, comment.Comments, commentEntity);
            }
        }

        private CommentEntity SaveComment(ISession session, StoryEntity storyEntity, Comment comment, CommentEntity parentcoCommentEntity)
        {
            var existingComment = _commentRepository.GetByCommentId(session, comment.CommentId);
            if (existingComment == null)
            {
                CommentEntity commentEntity = _storyMapper.ToCommentEntity(comment, parentcoCommentEntity, storyEntity);
                session.Save(commentEntity);
                Log.InfoFormat("Saved new comment, db id {0}", commentEntity.Id);
                return commentEntity;
            }

            // TODO update if there are vote differences
            // and/or raise event for other service
            // and/or add event in db instead of updating records
            return existingComment;
        }
    }
}