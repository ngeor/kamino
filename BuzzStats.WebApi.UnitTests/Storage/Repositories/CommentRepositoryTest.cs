using System.Collections.Generic;
using BuzzStats.WebApi.Storage.Entities;
using BuzzStats.WebApi.Storage.Repositories;
using BuzzStats.WebApi.UnitTests.Storage.TestHelpers;
using BuzzStats.WebApi.UnitTests.TestHelpers;
using Moq;
using NHibernate;
using NUnit.Framework;

namespace BuzzStats.WebApi.UnitTests.Storage.Repositories
{
    [TestFixture]
    public class CommentRepositoryTest
    {
#pragma warning disable 0649
        private Mock<ISession> _mockSession;
        [MockBehavior(MockBehavior.Strict)]
        private Mock<ICriteria> _mockCommentCriteria;
        private CommentRepository _commentRepository;
#pragma warning restore 0649

        [SetUp]
        public void SetUp()
        {
            MockHelper.InjectMocks(this);
            _mockSession.Setup(s => s.CreateCriteria<CommentEntity>()).Returns(_mockCommentCriteria.Object);
            _commentRepository = MockHelper.Create<CommentRepository>(this);
        }

        [Test]
        public void GetByCommentId()
        {
            // arrange
            var commentEntity = new CommentEntity
            {
                CommentId = 42
            };

            _mockCommentCriteria.SetupEq("CommentId", 42);
            _mockCommentCriteria.Setup(c => c.UniqueResult<CommentEntity>()).Returns(commentEntity);

            // act
            CommentEntity actualCommentEntity = _commentRepository.GetByCommentId(42);

            // assert
            Assert.AreEqual(commentEntity, actualCommentEntity);
        }

        [Test]
        public void GetRecent()
        {
            // arrange
            var comments = new List<CommentEntity>();
            _mockCommentCriteria.SetupOrderDesc("CreatedAt");
            _mockCommentCriteria.Setup(c => c.SetMaxResults(20)).Returns(_mockCommentCriteria.Object);
            _mockCommentCriteria.Setup(c => c.List<CommentEntity>()).Returns(comments);

            // act
            var result = _commentRepository.GetRecent();

            // assert
            Assert.AreEqual(comments, result);
        }
    }
}
