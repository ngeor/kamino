using System;
using System.Collections.Generic;
using NUnit.Framework;
using NUnitCompanion.Framework;
using NGSoftware.Common;
using BuzzStats.Data.NHibernate.Entities;

namespace BuzzStats.Data.NHibernate.Tests
{
    [TestFixture]
    [Category("Integration")]
    public class CommentStatsLayerTest : LayerTestBase
    {
        protected override void InitializeData()
        {
            // first insert it
            var story = Save(new StoryEntity
            {
                Title = "hello",
                Username = "nikolaos",
                StoryId = 100
            });

            var story2 = Save(new StoryEntity
            {
                Title = "my blog",
                Username = "ngeor",
                StoryId = 101
            });

            var comment = Save(new CommentEntity
            {
                CommentId = 200,
                Story = story,
                Username = "nikolaos",
                CreatedAt = new DateTime(2013, 1, 15),
                IsBuried = true
            });

            Save(new CommentEntity
            {
                CommentId = 2000,
                Story = story,
                ParentComment = comment,
                Username = "ngeor",
                CreatedAt = new DateTime(2014, 3, 1),
                IsBuried = true
            });

            Save(new CommentEntity
            {
                CommentId = 300,
                Story = story,
                Username = "nikolaos",
                CreatedAt = new DateTime(2014, 3, 1)
            });

            Save(new CommentEntity
            {
                CommentId = 201,
                Story = story2,
                Username = "nikolaos",
                CreatedAt = new DateTime(2014, 3, 1),
                IsBuried = true
            });

            Save(new CommentEntity
            {
                CommentId = 202,
                Story = story2,
                Username = "ngeor",
                CreatedAt = new DateTime(2014, 2, 28),
                VotesDown = 1
            });

            Save(new CommentEntity
            {
                CommentId = 444,
                Story = story,
                Username = "airplane",
                CreatedAt = new DateTime(2014, 2, 1),
                VotesUp = 4
            });
        }

        [Test]
        public void TestGetBuriedCommentCountPerUser()
        {
            var stats = DbSession.Comments.CountBuriedPerUser();
            Assert.AreEqual(2, stats["nikolaos"]);
            Assert.AreEqual(1, stats["ngeor"]);
            Assert.AreEqual(2, stats.Count);
        }

        [Test]
        public void TestGetBuriedCommentCountPerUserWithRange()
        {
            var stats = DbSession.Comments.CountBuriedPerUser(
                DateRange.Create(new DateTime(2014, 3, 1), new DateTime(2014, 3, 2)));
            Assert.AreEqual(1, stats["nikolaos"]);
            Assert.AreEqual(1, stats["ngeor"]);
            Assert.AreEqual(2, stats.Count);
        }

        [Test]
        public void TestGetVotesUpSumPerUser()
        {
            Dictionary<string, int> result = DbSession.Comments.SumVotesUpPerUser();
            Assert.IsNotNull(result);
            Assert.AreEqual(4, result["airplane"]);
            Assert.AreEqual(0, result["ngeor"]);
            Assert.AreEqual(0, result["nikolaos"]);
            Assert.AreEqual(3, result.Count);
        }

        [Test]
        public void TestGetVotesUpSumPerUserWithDateRange()
        {
            Dictionary<string, int> result = DbSession.Comments.SumVotesUpPerUser(
                DateRange.After(new DateTime(2014, 2, 1)));
            Assert.IsNotNull(result);
            Assert.AreEqual(4, result["airplane"]);
            Assert.AreEqual(0, result["ngeor"]);
            Assert.AreEqual(0, result["nikolaos"]);
            Assert.AreEqual(3, result.Count);
        }

        [Test]
        public void TestGetVotesDownSumPerUser()
        {
            Dictionary<string, int> result = DbSession.Comments.SumVotesDownPerUser();
            DictionaryAssert.AreEqual(new Dictionary<string, int>
            {
                {"airplane", 0},
                {"ngeor", 1},
                {"nikolaos", 0}
            }, result);
        }

        [Test]
        public void TestGetVotesDownSumPerUserWithDateRange()
        {
            Dictionary<string, int> result = DbSession.Comments.SumVotesDownPerUser(
                DateRange.After(new DateTime(2014, 3, 1)));
            DictionaryAssert.AreEqual(new Dictionary<string, int>
            {
                {"ngeor", 0},
                {"nikolaos", 0}
            }, result);
        }

        [Test]
        public void TestGetCommentCountPerUser()
        {
            Dictionary<string, int> stats = DbSession.Comments.CountPerUser();
            DictionaryAssert.AreEqual(new Dictionary<string, int>
            {
                {"nikolaos", 3},
                {"ngeor", 2},
                {"airplane", 1}
            }, stats);
        }

        [Test]
        public void TestGetCommentCountPerUserWithDateRange()
        {
            Dictionary<string, int> stats =
                DbSession.Comments.CountPerUser(
                    DateRange.Create(new DateTime(2013, 1, 15), new DateTime(2013, 1, 16)));
            DictionaryAssert.AreEqual(new Dictionary<string, int>
            {
                {"nikolaos", 1}
            }, stats);
        }
    }
}
