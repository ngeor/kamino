using System;
using System.Collections.Generic;
using System.Linq;
using BuzzStats.StorageWebApi.DTOs;
using BuzzStats.StorageWebApi.Entities;

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

        public virtual StoryVoteEntity[] ToStoryVoteEntities(Story story, StoryEntity storyEntity) =>
            (story.Voters ?? Enumerable.Empty<string>()).Select(v => new StoryVoteEntity
            {
                Story = storyEntity,
                Username = v
            }).ToArray();

        /// <summary>
        /// Returns all comments in a flat list. Parent comments will be before their children.
        /// </summary>
        public virtual CommentEntity[] ToCommentEntities(Story story, StoryEntity storyEntity)
            => ToCommentEntities(story.Comments, null, storyEntity).ToArray();

        private IEnumerable<CommentEntity> ToCommentEntities(
            IEnumerable<Comment> comments,
            CommentEntity parentCommentEntity,
            StoryEntity storyEntity)
            => (comments ?? Enumerable.Empty<Comment>()).SelectMany(c => ToCommentEntity(c, parentCommentEntity, storyEntity));

        private IEnumerable<CommentEntity> ToCommentEntity(Comment comment, CommentEntity parentCommentEntity,
            StoryEntity storyEntity)
        {
            var result = new CommentEntity
            {
                Story = storyEntity,
                CommentId = comment.CommentId,
                CreatedAt = comment.CreatedAt,
                IsBuried = comment.IsBuried,
                ParentComment = parentCommentEntity,
                Username = comment.Username,
                VotesDown = comment.VotesDown,
                VotesUp = comment.VotesUp
            };

            return new[] {result}.Concat(ToCommentEntities(comment.Comments, result, storyEntity));
        }
    }
}