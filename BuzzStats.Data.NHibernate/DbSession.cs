using System;
using System.Reflection;
using log4net;
using NHibernate;
using NGSoftware.Common;

namespace BuzzStats.Data.NHibernate
{
    public class DbSession : IDbSession
    {
        private static readonly ILog Log = LogManager.GetLogger(
            MethodBase.GetCurrentMethod().DeclaringType);

        private ITransaction _transaction;
        private StoryDataLayer _storyDataLayer;
        private CommentDataLayer _commentDataLayer;
        private StoryVoteDataLayer _storyVoteDataLayer;
        private CommentVoteDataLayer _commentVoteDataLayer;
        private StoryPollHistoryDataLayer _storyPollHistoryDataLayer;

        private IWebPageRepository _webPageRepository;

        internal DbSession(ISession session)
        {
            Log.Debug("constructor");
            Session = session;
        }

        public IStoryDataLayer Stories
        {
            get { return InitializeDataLayer(ref _storyDataLayer, () => new StoryDataLayer(Session)); }
        }

        public ICommentDataLayer Comments
        {
            get { return InitializeDataLayer(ref _commentDataLayer, () => new CommentDataLayer(Session)); }
        }

        public IStoryVoteDataLayer StoryVotes
        {
            get { return InitializeDataLayer(ref _storyVoteDataLayer, () => new StoryVoteDataLayer(Session)); }
        }

        public ICommentVoteDataLayer CommentVotes
        {
            get { return InitializeDataLayer(ref _commentVoteDataLayer, () => new CommentVoteDataLayer(Session)); }
        }

        public IWebPageRepository WebPageRepository
        {
            get { return InitializeDataLayer(ref _webPageRepository, () => new WebPageRepository(Session)); }
        }

        public virtual IRecentActivityRepository RecentActivityRepository
        {
            get { throw new NotSupportedException("RecentActivityRepository not supported by this database provider"); }
        }

        public IStoryPollHistoryDataLayer StoryPollHistories
        {
            get
            {
                return InitializeDataLayer(ref _storyPollHistoryDataLayer,
                    () => new StoryPollHistoryDataLayer(Session));
            }
        }

        protected internal ISession Session { get; private set; }

        public void BeginTransaction()
        {
            Log.Debug("BeginTransaction");
            AssertNotInTransaction();
            AssertInSession();
            CreateTransaction();
        }

        public void Commit()
        {
            AssertInTransaction();
            Session.Flush();
            _transaction.Commit();
            DisposeTransaction();
        }

        public void Rollback()
        {
            AssertInTransaction();
            _transaction.Rollback();
            DisposeTransaction();
        }

        public void Dispose()
        {
            Log.Debug("Dispose");
            _storyDataLayer = null;
            _commentDataLayer = null;
            _storyVoteDataLayer = null;
            _commentVoteDataLayer = null;
            DisposeTransaction();
            Session.SafeDispose();
            Session = null;
        }

        private void DisposeTransaction()
        {
            _transaction.SafeDispose();
            _transaction = null;
        }

        private void AssertInTransaction()
        {
            if (_transaction == null)
            {
                throw new InvalidOperationException("No transaction");
            }
        }

        private void CreateTransaction()
        {
            Log.Debug("CreateTransaction");
            _transaction = Session.BeginTransaction();
        }

        private void AssertNotInTransaction()
        {
            if (_transaction != null)
            {
                throw new InvalidOperationException("Transaction already open");
            }
        }

        private void AssertInSession()
        {
            if (Session == null)
            {
                throw new InvalidOperationException("No session");
            }
        }

        protected T InitializeDataLayer<T>(ref T instance, Func<T> initializer) where T : class
        {
            AssertInSession();
            return instance ?? (instance = initializer());
        }
    }
}