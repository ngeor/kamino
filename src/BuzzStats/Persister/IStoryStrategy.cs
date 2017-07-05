//
//  IStoryStrategy.cs
//
//  Author:
//       ngeor
//
//  Copyright (c) 2014 ngeor

using BuzzStats.Data;
using BuzzStats.Parsing;

namespace BuzzStats.Persister
{
    interface IStoryStrategy
    {
        StoryData Initialize(StoryData story, Story parsedStory);
        StoryVoteData[] GetExistingStoryVotes(StoryData story);
        CommentData LoadComment(int commentId);
        UpdateResult EndStory(StoryData story);

        /// <summary>
        /// Called when a story vote is added or deleted.
        /// </summary>
        void StoryVoteAdded();

        void StoryVoteDeleted();

        void CommentAdded();

        void CommentVoteAdded();
    }
}