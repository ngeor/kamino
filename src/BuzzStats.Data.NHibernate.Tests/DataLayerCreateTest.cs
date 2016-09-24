// --------------------------------------------------------------------------------
// <copyright file="DataLayerCreateTest.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/09/04
// * Time: 1:27 μμ
// --------------------------------------------------------------------------------

using System;
using System.Linq;
using NHibernate;
using NUnit.Framework;

namespace BuzzStats.Data.NHibernate.Tests
{
    [TestFixture]
    [Category("Integration")]
    public class DataLayerCreateTest : LayerTestBase
    {
        [Test]
        public void TestCreateChildComment()
        {
            // the story we'll insert
            StoryData story = new StoryData
            {
                StoryId = 42,
                Title = "my site",
                Category = 1,
                CreatedAt = new DateTime(2013, 6, 25),
                DetectedAt = new DateTime(2013, 6, 26),
                Host = "ngeor.net",
                LastCheckedAt = new DateTime(2013, 6, 27),
                LastCommentedAt = new DateTime(2013, 6, 28),
                LastModifiedAt = new DateTime(2013, 6, 29),
                RemovedAt = new DateTime(2013, 6, 30),
                TotalChecks = 1,
                TotalUpdates = 2,
                Url = "http://ngeor.net/",
                Username = "nikolaos",
                VoteCount = 2
            };

            story = DbSession.Stories.Create(story);

            CommentData parentComment = new CommentData
            {
                CommentId = 50,
                CreatedAt = new DateTime(2013, 5, 23),
                Story = story,
                Username = "test1",
                DetectedAt = new DateTime(2013, 5, 20),
                IsBuried = false,
                VotesDown = 0,
                VotesUp = 3
            };

            parentComment = DbSession.Comments.Create(parentComment);

            CommentData input = new CommentData
            {
                CommentId = 100,
                CreatedAt = new DateTime(2013, 5, 25),
                Story = story,
                Username = "test2",
                DetectedAt = new DateTime(2013, 5, 26),
                IsBuried = false,
                VotesDown = 1,
                VotesUp = 2,
                ParentComment = parentComment
            };

            // make sure we got something back and it has a positive database id
            CommentData result = DbSession.Comments.Create(input);
            Assert.IsNotNull(result);

            // compare it with the input. Temporarily set Id to zero to ignore it during comparison
            Assert.AreEqual(input, result);

            // dig into database to see it is really there.
            Assert.AreEqual(1, Connection.ExecuteScalar("SELECT COUNT(*) FROM Comment WHERE CommentId=100"));

            // test also the LoadStory, it should return the same data back
            CommentData loadedComment = DbSession.Comments.Query(story, parentComment).Single();
            Assert.AreEqual(result, loadedComment);
        }

        [Test]
        public void TestCreateComment()
        {
            // the story we'll insert
            StoryData story = new StoryData
            {
                StoryId = 42,
                Title = "my site",
                Category = 1,
                CreatedAt = new DateTime(2013, 6, 25),
                DetectedAt = new DateTime(2013, 6, 26),
                Host = "ngeor.net",
                LastCheckedAt = new DateTime(2013, 6, 27),
                LastCommentedAt = new DateTime(2013, 6, 28),
                LastModifiedAt = new DateTime(2013, 6, 29),
                RemovedAt = new DateTime(2013, 6, 30),
                TotalChecks = 1,
                TotalUpdates = 2,
                Url = "http://ngeor.net/",
                Username = "nikolaos",
                VoteCount = 2
            };

            // make sure we got something back and it has a positive database id
            story = DbSession.Stories.Create(story);

            CommentData input = new CommentData
            {
                CommentId = 100,
                CreatedAt = new DateTime(2013, 5, 25),
                Story = story,
                Username = "test2",
                DetectedAt = new DateTime(2013, 5, 26),
                IsBuried = false,
                VotesDown = 1,
                VotesUp = 2
            };

            // make sure we got something back and it has a positive database id
            CommentData result = DbSession.Comments.Create(input);
            Assert.IsNotNull(result);

            // compare it with the input. Temporarily set Id to zero to ignore it during comparison
            Assert.AreEqual(input, result);

            // dig into database to see it is really there.
            Assert.AreEqual(1, Connection.ExecuteScalar("SELECT COUNT(*) FROM Comment"));

            // test also the LoadStory, it should return the same data back
            CommentData loadedComment = DbSession.Comments.Query(story, null).Single();
            Assert.AreEqual(result, loadedComment);
        }

        [Test]
        public void TestCreateCommentVote()
        {
            // the story we'll insert
            StoryData story = new StoryData
            {
                StoryId = 42,
                Title = "my site",
                Category = 1,
                CreatedAt = new DateTime(2013, 6, 25),
                DetectedAt = new DateTime(2013, 6, 26),
                Host = "ngeor.net",
                LastCheckedAt = new DateTime(2013, 6, 27),
                LastCommentedAt = new DateTime(2013, 6, 28),
                LastModifiedAt = new DateTime(2013, 6, 29),
                RemovedAt = new DateTime(2013, 6, 30),
                TotalChecks = 1,
                TotalUpdates = 2,
                Url = "http://ngeor.net/",
                Username = "nikolaos",
                VoteCount = 2
            };

            // make sure we got something back and it has a positive database id
            story = DbSession.Stories.Create(story);

            CommentData comment = new CommentData
            {
                CommentId = 100,
                CreatedAt = new DateTime(2013, 5, 25),
                Story = story,
                Username = "test2",
                DetectedAt = new DateTime(2013, 5, 26),
                IsBuried = false,
                VotesDown = 1,
                VotesUp = 2
            };

            comment = DbSession.Comments.Create(comment);

            CommentVoteData input = new CommentVoteData
            {
                Comment = comment,
                CreatedAt = new DateTime(2013, 6, 25),
                IsBuried = true,
                VotesDown = 5,
                VotesUp = 1
            };

            DbSession.CommentVotes.Create(input);

            // dig into database to see it is really there.
            Assert.AreEqual(1, Connection.ExecuteScalar("SELECT COUNT(*) FROM CommentVote"));

            // test also the LoadStory, it should return the same data back
            CommentVoteData loadedCommentVote = DbSession.CommentVotes.Query(comment).Single();
            Assert.AreEqual(input, loadedCommentVote);
        }

        [Test]
        public void TestCreateStoryVote()
        {
            // the story we'll insert
            StoryData story = new StoryData
            {
                StoryId = 42,
                Title = "my site",
                Category = 1,
                CreatedAt = new DateTime(2013, 6, 25),
                DetectedAt = new DateTime(2013, 6, 26),
                Host = "ngeor.net",
                LastCheckedAt = new DateTime(2013, 6, 27),
                LastCommentedAt = new DateTime(2013, 6, 28),
                LastModifiedAt = new DateTime(2013, 6, 29),
                RemovedAt = new DateTime(2013, 6, 30),
                TotalChecks = 1,
                TotalUpdates = 2,
                Url = "http://ngeor.net/",
                Username = "nikolaos",
                VoteCount = 2
            };

            // make sure we got something back and it has a positive database id
            story = DbSession.Stories.Create(story);

            StoryVoteData input = new StoryVoteData
            {
                CreatedAt = new DateTime(2013, 5, 25),
                Story = story,
                Username = "test2"
            };

            DbSession.StoryVotes.Create(input);

            // dig into database to see it is really there.
            Assert.AreEqual(1, Connection.ExecuteScalar("SELECT COUNT(*) FROM StoryVote"));

            // test also the LoadStory, it should return the same data back
            StoryVoteData loadedStoryVote = DbSession.StoryVotes.Query(story).Single();
            Assert.AreEqual(input, loadedStoryVote);
        }

        [Test]
        public void TestCreateStoryVoteWithNonExistingStory()
        {
            // the story we'll insert
            StoryData story = new StoryData
            {
                StoryId = 42,
                Title = "my site",
                Category = 1,
                CreatedAt = new DateTime(2013, 6, 25),
                DetectedAt = new DateTime(2013, 6, 26),
                Host = "ngeor.net",
                LastCheckedAt = new DateTime(2013, 6, 27),
                LastCommentedAt = new DateTime(2013, 6, 28),
                LastModifiedAt = new DateTime(2013, 6, 29),
                RemovedAt = new DateTime(2013, 6, 30),
                TotalChecks = 1,
                TotalUpdates = 2,
                Url = "http://ngeor.net/",
                Username = "nikolaos",
                VoteCount = 2
            };

            StoryVoteData input = new StoryVoteData
            {
                CreatedAt = new DateTime(2013, 5, 25),
                Story = story,
                Username = "test2"
            };

            Assert.Throws<ObjectNotFoundException>(
                () => DbSession.StoryVotes.Create(input));
        }

        [Test]
        public void TestCreateStoryVoteWithTransientStory()
        {
            // the story we'll insert
            StoryData story = new StoryData
            {
                StoryId = 42,
                Title = "my site",
                Category = 1,
                CreatedAt = new DateTime(2013, 6, 25),
                DetectedAt = new DateTime(2013, 6, 26),
                Host = "ngeor.net",
                LastCheckedAt = new DateTime(2013, 6, 27),
                LastCommentedAt = new DateTime(2013, 6, 28),
                LastModifiedAt = new DateTime(2013, 6, 29),
                RemovedAt = new DateTime(2013, 6, 30),
                TotalChecks = 1,
                TotalUpdates = 2,
                Url = "http://ngeor.net/",
                Username = "nikolaos",
                VoteCount = 2
            };

            StoryVoteData input = new StoryVoteData
            {
                CreatedAt = new DateTime(2013, 5, 25),
                Story = story,
                Username = "test2"
            };

            Assert.Throws<ObjectNotFoundException>(
                () => DbSession.StoryVotes.Create(input));
        }
    }
}
