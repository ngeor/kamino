// --------------------------------------------------------------------------------
// <copyright file="LayerTestBase.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/09/04
// * Time: 1:27 μμ
// --------------------------------------------------------------------------------

using System;
using System.Configuration;
using System.Data;
using NHibernate;
using NUnit.Framework;
using NGSoftware.Common;
using BuzzStats.Data.NHibernate.Entities;

namespace BuzzStats.Data.NHibernate.Tests
{
    /// <summary>
    /// Base class for database tests.
    /// These tests use an actual database.
    /// </summary>
    public abstract class LayerTestBase
    {
        private readonly DbContextHolder dbContextHolder = new DbContextHolder();

        protected IDbSession DbSession { get; private set; }

        private ISession Session
        {
            get { return ((DbSession) DbSession).Session; }
        }

        protected IDbConnection Connection
        {
            get { return Session.Connection; }
        }

        [SetUp]
        public virtual void SetUp()
        {
            TestableDateTime.UtcNowStrategy = () => new DateTime(2014, 2, 13);

            CreateSession();
            Connection.ExecuteNonQuery("DELETE FROM StoryPollHistory");
            Connection.ExecuteNonQuery("DELETE FROM CommentVote");
            Connection.ExecuteNonQuery("DELETE FROM Comment WHERE ParentComment_Id IS NOT NULL");
            Connection.ExecuteNonQuery("DELETE FROM Comment");
            Connection.ExecuteNonQuery("DELETE FROM StoryVote");
            Connection.ExecuteNonQuery("DELETE FROM Story");

            InitializeData();
            DisposeSession();
            CreateSession();
        }

        [TearDown]
        public virtual void TearDown()
        {
            TestableDateTime.UtcNowStrategy = null;
            DisposeSession();
            dbContextHolder.TearDown();
        }

        protected virtual void InitializeData()
        {
        }

        protected T Save<T>(T obj) where T : class
        {
            Session.Save(obj);
            return obj;
        }

        protected CommentEntity SaveComment(StoryEntity story, int commentId, string username, DateTime createdAt,
            CommentEntity parentComment = null)
        {
            CommentEntity result = new CommentEntity
            {
                CommentId = commentId,
                CreatedAt = createdAt,
                DetectedAt = TestableDateTime.UtcNow,
                IsBuried = false,
                ParentComment = parentComment,
                Story = story,
                Username = username,
                VotesUp = 0,
                VotesDown = 0
            };

            return Save(result);
        }

        protected CommentVoteEntity SaveCommentVote(CommentEntity comment, int votesUp, int votesDown, bool isBuried,
            DateTime createdAt)
        {
            CommentVoteEntity result = new CommentVoteEntity
            {
                Comment = comment,
                CreatedAt = createdAt,
                IsBuried = isBuried,
                VotesDown = votesDown,
                VotesUp = votesUp
            };
            Session.Save(result);
            return result;
        }

        protected StoryEntity SaveStory(
            int storyId,
            string title,
            string username,
            DateTime createdAt,
            int voteCount = 1,
            DateTime? removedAt = null,
            DateTime? lastCheckedAt = null,
            DateTime? lastModifiedAt = null,
            string host = null,
            int totalChecks = 1)
        {
            StoryEntity story = new StoryEntity
            {
                StoryId = storyId,
                Title = title,
                Username = username,
                CreatedAt = createdAt,
                VoteCount = voteCount,
                RemovedAt = removedAt,
                LastCheckedAt = lastCheckedAt ?? TestableDateTime.UtcNow,
                LastModifiedAt = lastModifiedAt ?? TestableDateTime.UtcNow,
                Host = host,
                TotalChecks = totalChecks
            };

            return Save(story);
        }

        protected StoryVoteEntity SaveStoryVote(StoryEntity story, string username, DateTime createdAt)
        {
            return Save(new StoryVoteEntity
            {
                Story = story,
                Username = username,
                CreatedAt = createdAt
            });
        }

        protected void FlushAndReopenSession()
        {
            Session.Flush();
            DisposeSession();
            CreateSession();
        }

        private void CreateSession()
        {
            DbSession = dbContextHolder.DbContext.OpenSession();
        }

        private void DisposeSession()
        {
            DbSession.Dispose();
        }

        private class DbContextHolder
        {
            private IDbContext _dbContext;

            public IDbContext DbContext
            {
                get { return _dbContext ?? (_dbContext = CreateDbContext()); }
            }

            public void TearDown()
            {
                _dbContext.Dispose();
                _dbContext = null;
            }

            private IDbContext CreateDbContext()
            {
                return new DbContextFactory(ConfigurationManager.ConnectionStrings["sqlite"]).Create();
            }
        }
    }
}
