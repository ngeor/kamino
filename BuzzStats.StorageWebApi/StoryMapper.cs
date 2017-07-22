using System.Collections.Generic;
using System.Linq;
using BuzzStats.StorageWebApi.DTOs;
using BuzzStats.StorageWebApi.Entities;
using FluentNHibernate.Data;

namespace BuzzStats.StorageWebApi
{
    /// <summary>
    /// Maps story DTOs to story entities.
    /// </summary>
    public class StoryMapper
    {
        public virtual StoryEntity ToStoryEntity(Story story) => new StoryEntity
        {
            Title = story.Title,
            StoryId = story.StoryId,
            Url = story.Url,
            Username = story.Username,
            CreatedAt = story.CreatedAt,
            Category = story.Category,
        };

        public virtual StoryEntity UpdateStoryEntity(StoryEntity existing, StoryEntity updated)
        {
            // TODO Update changed fields
            existing.Title = updated.Title;
            return existing;
        }

        public virtual StoryVoteEntity[] ToStoryVoteEntities(Story story, StoryEntity storyEntity) =>
            (story.Voters ?? Enumerable.Empty<string>()).Select(v => new StoryVoteEntity
            {
                Story = storyEntity,
                Username = v
            }).ToArray();

        /// <summary>
        /// Returns all comments.
        /// </summary>
        public virtual CommentEntityHolder[] ToCommentEntities(Story story, StoryEntity storyEntity)
            => ToCommentEntities(story.Comments, new CommentEntityHolder(), storyEntity).ToArray();

        private IEnumerable<CommentEntityHolder> ToCommentEntities(
            IEnumerable<Comment> comments,
            CommentEntityHolder parentCommentEntity,
            StoryEntity storyEntity)
            => (comments ?? Enumerable.Empty<Comment>()).Select(c => ToCommentEntity(c, parentCommentEntity, storyEntity));

        private CommentEntityHolder ToCommentEntity(
            Comment comment,
            CommentEntityHolder parentCommentEntity,
            StoryEntity storyEntity)
        {
            var result = new CommentEntity
            {
                Story = storyEntity,
                CommentId = comment.CommentId,
                CreatedAt = comment.CreatedAt,
                IsBuried = comment.IsBuried,
                ParentComment = parentCommentEntity.Entity,
                Username = comment.Username,
                VotesDown = comment.VotesDown,
                VotesUp = comment.VotesUp
            };

            var entityHolder = new CommentEntityHolder
            {
                Entity = result
            };

            entityHolder.Children = ToCommentEntities(comment.Comments, entityHolder, storyEntity).ToList();

            return entityHolder;
        }
    }
}