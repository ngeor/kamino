using System;

namespace BuzzStats.Data
{
    public interface IDbSession : IDisposable
    {
        IStoryDataLayer Stories { get; }

        IStoryVoteDataLayer StoryVotes { get; }

        ICommentDataLayer Comments { get; }

        ICommentVoteDataLayer CommentVotes { get; }

        IStoryPollHistoryDataLayer StoryPollHistories { get; }

        IRecentActivityRepository RecentActivityRepository { get; }

        IWebPageRepository WebPageRepository { get; }

        void BeginTransaction();

        void Commit();

        void Rollback();
    }
}
