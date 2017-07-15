using BuzzStats.StorageWebApi.DTOs;
using BuzzStats.StorageWebApi.Entities;
using BuzzStats.StorageWebApi.Repositories;
using Moq;
using NHibernate;
using NUnit.Framework;

namespace BuzzStats.StorageWebApi.UnitTests
{
    [TestFixture]
    public class CommentUpdaterTest
    {
        private Mock<ISession> _mockSession;
        private Mock<StoryMapper> _mockStoryMapper;
        private Mock<CommentRepository> _mockCommentRepository;
        private CommentUpdater _commentUpdater;

        [SetUp]
        public void SetUp()
        {
            _mockSession = new Mock<ISession>();
            _mockStoryMapper = new Mock<StoryMapper>();
            _mockCommentRepository = new Mock<CommentRepository>(MockBehavior.Strict);
            _commentUpdater = new CommentUpdater(_mockStoryMapper.Object, _mockCommentRepository.Object);
        }

        [Test]
        public void SaveComments()
        {
            // arrange
            var story = new Story();
            var storyEntity = new StoryEntity();
            var commentEntities = new[]
            {
                new CommentEntity()
            };

            _mockStoryMapper.Setup(p => p.ToCommentEntities(story, storyEntity)).Returns(commentEntities);

            // act
            _commentUpdater.SaveComments(_mockSession.Object, story, storyEntity);

            // assert
            _mockSession.Verify(s => s.SaveOrUpdate(commentEntities[0]));
        }
    }
}