using System;

namespace BuzzStats.Data.Dapper
{
    public class DbSession : IDbSession
    {
        public IStoryDataLayer Stories
        {
            get
            {
                return new StoryDataLayer(null);
            }
        }

        public IStoryVoteDataLayer StoryVotes
        {
            get { throw new NotImplementedException(); }
        }

        public ICommentDataLayer Comments
        {
            get { throw new NotImplementedException(); }
        }

        public ICommentVoteDataLayer CommentVotes
        {
            get { throw new NotImplementedException(); }
        }

        public IStoryPollHistoryDataLayer StoryPollHistories
        {
            get { throw new NotImplementedException(); }
        }

        public IRecentActivityRepository RecentActivityRepository
        {
            get { throw new NotImplementedException(); }
        }

        public IWebPageRepository WebPageRepository
        {
            get { throw new NotImplementedException(); }
        }

        public void BeginTransaction()
        {
            throw new NotImplementedException();
        }

        public void Commit()
        {
            throw new NotImplementedException();
        }

        public void Rollback()
        {
            throw new NotImplementedException();
        }

        public void Dispose()
        {
        }
    }
}
