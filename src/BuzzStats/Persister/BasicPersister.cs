//
//  NextStoryPersister.cs
//
//  Author:
//       ngeor
//
//  Copyright (c) 2014 ngeor

using System;
using System.Collections.Generic;
using System.Linq;
using NGSoftware.Common;
using BuzzStats.Data;
using BuzzStats.Parsing;

namespace BuzzStats.Persister
{
    public sealed class BasicPersister : IDbPersister
    {
        private IStoryStrategy StoryStrategy { get; set; }

        private StoryData Story { get; set; }

        public IDbSession DbSession { get; set; }

        public PersisterResult MarkAsUnmodified(int storyId)
        {
            Story = DbSession.Stories.Read(storyId);
            if (Story != null)
            {
                Story.TotalChecks++;
                Story.LastCheckedAt = TestableDateTime.UtcNow;
                DbSession.Stories.Update(Story);
            }

            return new PersisterResult(Story, UpdateResult.NoChanges);
        }

        public PersisterResult Save(Story parsedStory)
        {
            if (parsedStory == null)
            {
                throw new ArgumentNullException("story");
            }

            if (parsedStory.StoryId <= 0)
            {
                throw new ArgumentException(string.Format("Invalid story id {0}", parsedStory.StoryId));
            }

            Story = DbSession.Stories.Read(parsedStory.StoryId);
            return parsedStory.IsRemoved ? SaveRemovedStory(parsedStory) : SaveNonRemovedStory(parsedStory);
        }

        private PersisterResult SaveRemovedStory(Story parsedStory)
        {
            if (Story != null)
            {
                Story.TotalChecks++;
                Story.LastCheckedAt = TestableDateTime.UtcNow;
                Story.TotalUpdates++;
                Story.LastModifiedAt = TestableDateTime.UtcNow;
                Story.RemovedAt = TestableDateTime.UtcNow;
                DbSession.Stories.Update(Story);
            }

            return new PersisterResult(Story, Story == null ? UpdateResult.NoChanges : UpdateResult.Removed);
        }

        private PersisterResult SaveNonRemovedStory(Story parsedStory)
        {
            if (parsedStory.Voters == null || parsedStory.Voters.Length <= 0)
            {
                throw new ArgumentException(string.Format("No story voters for story {0}", parsedStory.StoryId));
            }

            StoryStrategy = Story == null
                ? (IStoryStrategy) new NewStoryStrategy(DbSession)
                : new ExistingStoryStrategy(DbSession);

            Story = StoryStrategy.Initialize(Story, parsedStory);
            StoryVoteData[] existingStoryVotes = StoryStrategy.GetExistingStoryVotes(Story);

            // create votes if they don't exist already
            CreateStoryVotes(parsedStory.Voters.Except(existingStoryVotes.Select(v => v.Username)));

            // delete votes that existed but they're gone
            DeleteStoryVotes(existingStoryVotes.Select(v => v.Username).Except(parsedStory.Voters));

            ProcessComments(parsedStory.Comments);
            UpdateResult changes = StoryStrategy.EndStory(Story);
            return new PersisterResult(Story, changes);
        }

        private void ProcessComments(IEnumerable<Comment> comments, CommentData parentComment = null)
        {
            foreach (Comment c in comments ?? Enumerable.Empty<Comment>())
            {
                ProcessComment(c, parentComment);
            }
        }

        private void ProcessComment(Comment c, CommentData parentComment)
        {
            CommentData comment = StoryStrategy.LoadComment(c.CommentId);

            if (comment == null)
            {
                comment = new CommentData
                {
                    CommentId = c.CommentId,
                    CreatedAt = c.CreatedAt,
                    DetectedAt = TestableDateTime.UtcNow,
                    IsBuried = c.IsBuried,
                    ParentComment = parentComment,
                    Story = Story,
                    Username = c.Username,
                    VotesDown = c.VotesDown,
                    VotesUp = c.VotesUp
                };

                comment = DbSession.Comments.Create(comment);
                CreateCommentVote(comment, c.VotesUp, c.VotesDown, c.IsBuried);
                StoryStrategy.CommentAdded();
            }
            else
            {
                if (comment.VotesUp != c.VotesUp || comment.VotesDown != c.VotesDown || comment.IsBuried != c.IsBuried)
                {
                    comment.VotesUp = c.VotesUp;
                    comment.VotesDown = c.VotesDown;
                    comment.IsBuried = c.IsBuried;
                    DbSession.Comments.Update(comment);
                    CreateCommentVote(comment, c.VotesUp, c.VotesDown, c.IsBuried);
                    StoryStrategy.CommentVoteAdded();
                }
            }

            ProcessComments(c.Comments, comment);
        }

        private void CreateCommentVote(CommentData comment, int votesUp, int votesDown, bool isBuried)
        {
            var commentVote = new CommentVoteData
            {
                Comment = comment,
                CreatedAt = TestableDateTime.UtcNow,
                IsBuried = isBuried,
                VotesDown = votesDown,
                VotesUp = votesUp
            };

            DbSession.CommentVotes.Create(commentVote);
        }

        private void DeleteStoryVotes(IEnumerable<string> voters)
        {
            foreach (string voter in voters)
            {
                DbSession.StoryVotes.Delete(Story, voter);
                StoryStrategy.StoryVoteDeleted();
            }
        }

        private void CreateStoryVotes(IEnumerable<string> voters)
        {
            foreach (var voter in voters)
            {
                CreateStoryVote(voter);
            }
        }

        private void CreateStoryVote(string voter)
        {
            DbSession.StoryVotes.Create(new StoryVoteData
            {
                Username = voter,
                CreatedAt = TestableDateTime.UtcNow,
                Story = Story
            });

            StoryStrategy.StoryVoteAdded();
        }
    }
}
