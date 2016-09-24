//
//  ExistingStoryStrategy.cs
//
//  Author:
//       ngeor
//
//  Copyright (c) 2014 ngeor

using NGSoftware.Common;
using BuzzStats.Data;
using BuzzStats.Parsing;

namespace BuzzStats.Persister
{
    class ExistingStoryStrategy : IStoryStrategy
    {
        public ExistingStoryStrategy(IDbSession dbSession)
        {
            DbSession = dbSession;
        }

        public IDbSession DbSession { get; set; }

        public StoryData Initialize(StoryData story, Story parsedStory)
        {
            story.TotalChecks++;
            story.LastCheckedAt = TestableDateTime.UtcNow;
            story.LastCommentedAt = parsedStory.LastCommentedAt();
            story.VoteCount = parsedStory.Voters.Length;
            return story;
        }

        public CommentData LoadComment(int commentId)
        {
            return DbSession.Comments.Read(commentId);
        }

        public StoryVoteData[] GetExistingStoryVotes(StoryData story)
        {
            return DbSession.StoryVotes.Query(story);
        }

        public UpdateResult EndStory(StoryData story)
        {
            if (Changes != UpdateResult.NoChanges)
            {
                story.TotalUpdates++;
                story.LastModifiedAt = TestableDateTime.UtcNow;
            }

            DbSession.Stories.Update(story);
            return Changes;
        }

        public void LastCommentedAtChanged()
        {
        }

        public void StoryVoteAdded()
        {
            Changes |= UpdateResult.NewVotes;
        }

        public void StoryVoteDeleted()
        {
            Changes |= UpdateResult.LessVotes;
        }

        public void CommentAdded()
        {
            Changes |= UpdateResult.NewComments;
        }

        public void CommentVoteAdded()
        {
            Changes |= UpdateResult.NewCommentVotes;
        }

        private UpdateResult Changes { get; set; }
    }
}
