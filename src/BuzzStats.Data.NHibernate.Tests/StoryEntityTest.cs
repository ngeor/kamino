using System;
using NUnit.Framework;
using BuzzStats.Data.NHibernate.Entities;

namespace BuzzStats.Data.NHibernate.Tests
{
    [TestFixture]
    [Category("Entity")]
    public class StoryEntityTest
    {
        [Test]
        public void TestMapEntityToData()
        {
            StoryEntity entity = new StoryEntity
            {
                Category = 3,
                CreatedAt = new DateTime(2014, 12, 05),
                DetectedAt = new DateTime(2014, 1, 1),
                Host = "ngeor.net",
                Id = 1000,
                LastCheckedAt = new DateTime(2014, 08, 09),
                LastCommentedAt = new DateTime(2014, 3, 1),
                LastModifiedAt = new DateTime(2014, 11, 11),
                RemovedAt = new DateTime(2014, 5, 1),
                StoryId = 42,
                Title = "my blog",
                TotalChecks = 13,
                TotalUpdates = 11,
                Url = "http:/ngeor.net/blog",
                Username = "ngeor",
                VoteCount = 71
            };

            StoryData expected = new StoryData
            {
                Category = 3,
                CreatedAt = new DateTime(2014, 12, 05),
                DetectedAt = new DateTime(2014, 1, 1),
                Host = "ngeor.net",
                LastCheckedAt = new DateTime(2014, 08, 09),
                LastCommentedAt = new DateTime(2014, 3, 1),
                LastModifiedAt = new DateTime(2014, 11, 11),
                RemovedAt = new DateTime(2014, 5, 1),
                StoryId = 42,
                Title = "my blog",
                TotalChecks = 13,
                TotalUpdates = 11,
                Url = "http:/ngeor.net/blog",
                Username = "ngeor",
                VoteCount = 71
            };

            StoryData actual = entity.ToData();

            Assert.AreEqual(expected, actual);

            Assert.AreEqual(expected.Category, actual.Category);
            Assert.AreEqual(expected.CreatedAt, actual.CreatedAt);
            Assert.AreEqual(expected.DetectedAt, actual.DetectedAt);
            Assert.AreEqual(expected.Host, actual.Host);
            Assert.AreEqual(expected.LastCheckedAt, actual.LastCheckedAt);
            Assert.AreEqual(expected.LastCommentedAt, actual.LastCommentedAt);
            Assert.AreEqual(expected.LastModifiedAt, actual.LastModifiedAt);
            Assert.AreEqual(expected.RemovedAt, actual.RemovedAt);
            Assert.AreEqual(expected.StoryId, actual.StoryId);
            Assert.AreEqual(expected.Title, actual.Title);
            Assert.AreEqual(expected.TotalChecks, actual.TotalChecks);
            Assert.AreEqual(expected.TotalUpdates, actual.TotalUpdates);
        }

        [Test]
        public void TestMapDataToEntity()
        {
            StoryData data = new StoryData
            {
                Category = 3,
                CreatedAt = new DateTime(2014, 12, 05),
                DetectedAt = new DateTime(2014, 1, 1),
                Host = "ngeor.net",
                LastCheckedAt = new DateTime(2014, 08, 09),
                LastCommentedAt = new DateTime(2014, 3, 1),
                LastModifiedAt = new DateTime(2014, 11, 11),
                RemovedAt = new DateTime(2014, 5, 1),
                StoryId = 42,
                Title = "my blog",
                TotalChecks = 13,
                TotalUpdates = 11,
                Url = "http:/ngeor.net/blog",
                Username = "ngeor",
                VoteCount = 71
            };

            StoryEntity expected = new StoryEntity
            {
                Category = 3,
                CreatedAt = new DateTime(2014, 12, 05),
                DetectedAt = new DateTime(2014, 1, 1),
                Host = "ngeor.net",
                Id = 0,
                LastCheckedAt = new DateTime(2014, 08, 09),
                LastCommentedAt = new DateTime(2014, 3, 1),
                LastModifiedAt = new DateTime(2014, 11, 11),
                RemovedAt = new DateTime(2014, 5, 1),
                StoryId = 42,
                Title = "my blog",
                TotalChecks = 13,
                TotalUpdates = 11,
                Url = "http:/ngeor.net/blog",
                Username = "ngeor",
                VoteCount = 71
            };

            StoryEntity actual = data.ToEntity();

            Assert.AreEqual(expected.Category, actual.Category);
            Assert.AreEqual(expected.CreatedAt, actual.CreatedAt);
            Assert.AreEqual(expected.DetectedAt, actual.DetectedAt);
            Assert.AreEqual(expected.Host, actual.Host);
            Assert.AreEqual(expected.Id, actual.Id);
            Assert.AreEqual(expected.LastCheckedAt, actual.LastCheckedAt);
            Assert.AreEqual(expected.LastCommentedAt, actual.LastCommentedAt);
            Assert.AreEqual(expected.LastModifiedAt, actual.LastModifiedAt);
            Assert.AreEqual(expected.RemovedAt, actual.RemovedAt);
            Assert.AreEqual(expected.StoryId, actual.StoryId);
            Assert.AreEqual(expected.Title, actual.Title);
            Assert.AreEqual(expected.TotalChecks, actual.TotalChecks);
            Assert.AreEqual(expected.TotalUpdates, actual.TotalUpdates);
        }
    }
}
