//
//  NewStoryStrategy.cs
//
//  Author:
//       ngeor
//
//  Copyright (c) 2014 ngeor

using BuzzStats.Data;
using BuzzStats.Parsing;
using NodaTime;

namespace BuzzStats.Persister
{
    class NewStoryStrategy : IStoryStrategy
    {
        public NewStoryStrategy(IDbSession dbSession, IClock clock)
        {
            DbSession = dbSession;
            Clock = clock;
        }

        public IClock Clock { get; set; }

        public IDbSession DbSession { get; private set; }

        private UpdateResult Changes { get; set; }

        public StoryData Initialize(StoryData story, Story parsedStory)
        {
            story = new StoryData
            {
                StoryId = parsedStory.StoryId,
                Category = parsedStory.Category,
                Url = parsedStory.Url,
                Title = parsedStory.Title,
                CreatedAt = parsedStory.CreatedAt,
                DetectedAt = Clock.GetCurrentInstant().ToDateTimeUtc(),
                Host = HostUtils.GetHost(parsedStory.Url, parsedStory.StoryId),
                Username = parsedStory.Username,
                VoteCount = parsedStory.Voters.Length,
                TotalChecks = 1,
                TotalUpdates = 1,
                LastCheckedAt = Clock.GetCurrentInstant().ToDateTimeUtc(),
                LastCommentedAt = parsedStory.LastCommentedAt(),
                LastModifiedAt = Clock.GetCurrentInstant().ToDateTimeUtc()
            };

            Changes |= UpdateResult.Created;
            if (story.VoteCount > 1)
            {
                Changes |= UpdateResult.NewVotes;
            }

            return DbSession.Stories.Create(story);
        }

        public CommentData LoadComment(int commentId)
        {
            return null;
        }

        public StoryVoteData[] GetExistingStoryVotes(StoryData story)
        {
            return new StoryVoteData[0];
        }

        public UpdateResult EndStory(StoryData story)
        {
            return Changes;
        }

        public void StoryVoteAdded()
        {
        }

        public void StoryVoteDeleted()
        {
        }

        public void CommentAdded()
        {
            Changes |= UpdateResult.NewComments;
        }

        public void CommentVoteAdded()
        {
            // should not be called
        }
    }
}