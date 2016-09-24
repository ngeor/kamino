using System;
using NUnit.Framework;
using NGSoftware.Common;
using BuzzStats.Data.NHibernate.Entities;

namespace BuzzStats.Data.NHibernate.Tests
{
    [TestFixture]
    [Category("Entity")]
    public class RecentActivityEntityTest
    {
        [Test]
        public void TestMapEntityToData()
        {
            TestableDateTime.UtcNowStrategy = () => new DateTime(2014, 12, 3);

            var entity = new RecentActivityEntity
            {
                CommentId = 42,
                CreatedAt = new DateTime(2014, 12, 1),
                DetectedAt = new DateTime(2014, 12, 2),
                StoryId = 100,
                Title = "my blog",
                Username = "ngeor",
                What = 2
            };

            var expected = new RecentActivity
            {
                CommentId = 42,
                Age = TimeSpan.FromDays(2),
                DetectedAtAge = TimeSpan.FromDays(1),
                StoryId = 100,
                StoryTitle = "my blog",
                What = RecentActivityKind.NewStoryVote,
                Who = "ngeor"
            };

            var actual = entity.ToData();
            Assert.AreEqual(expected, actual);
            Assert.AreEqual(expected.CommentId, actual.CommentId);
            Assert.AreEqual(expected.Age, actual.Age);
            Assert.AreEqual(expected.DetectedAtAge, actual.DetectedAtAge);
            Assert.AreEqual(expected.StoryId, actual.StoryId);
            Assert.AreEqual(expected.StoryTitle, actual.StoryTitle);
            Assert.AreEqual(expected.What, actual.What);
            Assert.AreEqual(expected.Who, actual.Who);
        }
    }
}
