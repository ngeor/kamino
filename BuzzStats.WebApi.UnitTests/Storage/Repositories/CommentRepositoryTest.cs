using BuzzStats.StorageWebApi.Entities;
using BuzzStats.StorageWebApi.Repositories;
using BuzzStats.StorageWebApi.UnitTests.TestHelpers;
using Moq;
using NHibernate;
using NHibernate.Criterion;
using NUnit.Framework;

namespace BuzzStats.StorageWebApi.UnitTests.Repositories
{
    [TestFixture]
    public class CommentRepositoryTest
    {
        private Mock<ISession> _mockSession;
        private Mock<ICriteria> _mockCommentCriteria;
        private CommentRepository _commentRepository;

        [SetUp]
        public void SetUp()
        {
            _mockSession = new Mock<ISession>();
            _mockCommentCriteria = new Mock<ICriteria>(MockBehavior.Strict);
            _mockSession.Setup(s => s.CreateCriteria<CommentEntity>()).Returns(_mockCommentCriteria.Object);
            _commentRepository = new CommentRepository();
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
            CommentEntity actualCommentEntity = _commentRepository.GetByCommentId(_mockSession.Object, 42);
            
            // assert
            Assert.AreEqual(commentEntity, actualCommentEntity);
        }
    }
}