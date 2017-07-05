using System;
using NUnit.Framework;
using NGSoftware.Common;
using BuzzStats.Data.NHibernate.Entities;

namespace BuzzStats.Data.NHibernate.Tests
{
    [TestFixture]
    [Category("Entity")]
    public class CommentVoteEntityTest
    {
        [SetUp]
        public void SetUp()
        {
            TestableDateTime.UtcNowStrategy = () => new DateTime(2014, 2, 13);
        }

        [Test]
        public void TestMapEntityToData()
        {
            CommentVoteEntity entity = new CommentVoteEntity
            {
                Comment = new CommentEntity(),
                CreatedAt = new DateTime(2014, 12, 05),
                Id = 123,
                IsBuried = true,
                VotesDown = 5,
                VotesUp = 6
            };

            CommentVoteData expected = new CommentVoteData
            {
                Comment = new CommentData(),
                CreatedAt = new DateTime(2014, 12, 05),
                IsBuried = true,
                VotesDown = 5,
                VotesUp = 6
            };

            CommentVoteData actual = entity.ToData();
            Assert.AreEqual(expected, actual);
            Assert.AreEqual(expected.Comment, actual.Comment);
            Assert.AreEqual(expected.CreatedAt, actual.CreatedAt);
            Assert.AreEqual(expected.IsBuried, actual.IsBuried);
            Assert.AreEqual(expected.VotesDown, actual.VotesDown);
            Assert.AreEqual(expected.VotesUp, actual.VotesUp);
        }

        [Test]
        public void TestMapDataToEntity()
        {
            CommentVoteData data = new CommentVoteData
            {
                Comment = new CommentData {CommentId = 42},
                CreatedAt = new DateTime(2014, 12, 05),
                IsBuried = true,
                VotesDown = 6,
                VotesUp = 11
            };

            CommentVoteEntity expected = new CommentVoteEntity
            {
                Comment = null,
                CreatedAt = new DateTime(2014, 12, 05),
                IsBuried = true,
                VotesDown = 6,
                VotesUp = 11
            };

            CommentVoteEntity actual = data.ToEntity();
            Assert.AreEqual(expected.Comment, actual.Comment);
            Assert.AreEqual(expected.CreatedAt, actual.CreatedAt);
            Assert.AreEqual(expected.IsBuried, actual.IsBuried);
            Assert.AreEqual(expected.VotesDown, actual.VotesDown);
            Assert.AreEqual(expected.VotesUp, actual.VotesUp);
        }
    }
}