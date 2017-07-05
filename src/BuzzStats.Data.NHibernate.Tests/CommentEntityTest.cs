using System;
using NUnit.Framework;
using BuzzStats.Data.NHibernate.Entities;

namespace BuzzStats.Data.NHibernate.Tests
{
    [TestFixture]
    [Category("Entity")]
    public class CommentEntityTest
    {
        [Test]
        public void TestMapEntityToData()
        {
            CommentEntity entity = new CommentEntity
            {
                CommentId = 42,
                CreatedAt = new DateTime(2014, 12, 05),
                DetectedAt = new DateTime(2014, 12, 06),
                Id = 100,
                IsBuried = true,
                ParentComment = new CommentEntity(),
                Story = new StoryEntity(),
                Username = "ngeor",
                VotesDown = 5,
                VotesUp = 6
            };

            CommentData expected = new CommentData
            {
                CommentId = 42,
                CreatedAt = new DateTime(2014, 12, 05),
                DetectedAt = new DateTime(2014, 12, 06),
                IsBuried = true,
                ParentComment = new CommentData(),
                Story = new StoryData(),
                Username = "ngeor",
                VotesDown = 5,
                VotesUp = 6
            };

            CommentData actual = entity.ToData();
            Assert.AreEqual(expected, actual);
            Assert.AreEqual(expected.CommentId, actual.CommentId);
            Assert.AreEqual(expected.CreatedAt, actual.CreatedAt);
            Assert.AreEqual(expected.DetectedAt, actual.DetectedAt);
            Assert.AreEqual(expected.IsBuried, actual.IsBuried);
            Assert.AreEqual(expected.Username, actual.Username);
            Assert.AreEqual(expected.VotesDown, actual.VotesDown);
            Assert.AreEqual(expected.VotesUp, actual.VotesUp);
        }

        [Test]
        public void TestMapDataToEntity()
        {
            CommentData data = new CommentData
            {
                CommentId = 42,
                CreatedAt = new DateTime(2014, 12, 05),
                DetectedAt = new DateTime(2014, 12, 06),
                IsBuried = true,
                ParentComment = new CommentData {CommentId = 40},
                Story = new StoryData {StoryId = 11},
                Username = "ngeor",
                VotesDown = 5,
                VotesUp = 6
            };

            CommentEntity expected = new CommentEntity
            {
                CommentId = 42,
                CreatedAt = new DateTime(2014, 12, 05),
                DetectedAt = new DateTime(2014, 12, 06),
                Id = 0,
                IsBuried = true,
                ParentComment = null,
                Story = null,
                Username = "ngeor",
                VotesDown = 5,
                VotesUp = 6
            };

            CommentEntity actual = data.ToEntity();
            Assert.AreEqual(expected.CommentId, actual.CommentId);
            Assert.AreEqual(expected.CreatedAt, actual.CreatedAt);
            Assert.AreEqual(expected.DetectedAt, actual.DetectedAt);
            Assert.AreEqual(expected.Id, actual.Id);
            Assert.AreEqual(expected.IsBuried, actual.IsBuried);
            Assert.AreEqual(expected.ParentComment, actual.ParentComment);
            Assert.AreEqual(expected.Story, actual.Story);
            Assert.AreEqual(expected.Username, actual.Username);
            Assert.AreEqual(expected.VotesDown, actual.VotesDown);
            Assert.AreEqual(expected.VotesUp, actual.VotesUp);
        }
    }
}