using BuzzStats.WebApi.DTOs;
using BuzzStats.WebApi.Storage.Entities;
using BuzzStats.WebApi.Storage.Repositories;
using log4net;
using NGSoftware.Common.Messaging;
using NHibernate;
using NodaTime;

namespace BuzzStats.WebApi.Storage
{
    public class CommentUpdater : ICommentUpdater
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof(CommentUpdater));
        private readonly StoryMapper _storyMapper;
        private readonly CommentRepository _commentRepository;
        private readonly IMessageBus _messageBus;
        private readonly IClock _clock;

        public CommentUpdater(StoryMapper storyMapper, CommentRepository commentRepository, IMessageBus messageBus, IClock clock)
        {
            _storyMapper = storyMapper;
            _commentRepository = commentRepository;
            _messageBus = messageBus;
            _clock = clock;
        }

        public void SaveComments(ISession session, Story story, StoryEntity storyEntity)
        {
            Log.DebugFormat("SaveComments of story {0}", story.StoryId);
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
                
                // TODO test child comments
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
                _messageBus.Publish(commentEntity);
                session.Save(new RecentActivityEntity
                {
                    CreatedAt = _clock.GetCurrentInstant().ToDateTimeUtc(),
                    Story = storyEntity,
                    Comment = commentEntity
                });
                return commentEntity;
            }

            // TODO update if there are vote differences
            // and/or raise event for other service
            // and/or add event in db instead of updating records
            return existingComment;
        }
    }
}