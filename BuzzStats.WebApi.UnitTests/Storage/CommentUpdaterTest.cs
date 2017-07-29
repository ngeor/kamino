using BuzzStats.CrawlerService.DTOs;
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
            var story = new Story
            {
                Comments = new[]
                {
                    new Comment
                    {
                        CommentId = 42
                    }
                }
            };
            
            var storyEntity = new StoryEntity();
            var commentEntities = new[]
            {
                new CommentEntity()
            };

            _mockStoryMapper.Setup(p => p.ToCommentEntity(story.Comments[0], null, storyEntity))
                .Returns(commentEntities[0]);
            _mockCommentRepository.Setup(p => p.GetByCommentId(_mockSession.Object, 42))
                .Returns((CommentEntity)null);

            // act
            _commentUpdater.SaveComments(_mockSession.Object, story, storyEntity);

            // assert
            _mockSession.Verify(s => s.Save(commentEntities[0]));
        }
    }
}