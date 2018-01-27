using System;
using BuzzStats.DTOs;
using BuzzStats.Parsing.DTOs;
using BuzzStats.WebApi.DTOs;
using BuzzStats.WebApi.Storage;
using BuzzStats.WebApi.Storage.Entities;
using BuzzStats.WebApi.Storage.Repositories;
using BuzzStats.WebApi.UnitTests.TestHelpers;
using Moq;
using NGSoftware.Common.Messaging;
using NHibernate;
using NUnit.Framework;

namespace BuzzStats.WebApi.UnitTests.Storage
{
    [TestFixture]
    public class CommentUpdaterTest
    {
#pragma warning disable 0649
        private Mock<ISession> _mockSession;
        private Mock<StoryMapper> _mockStoryMapper;
        private Mock<ICommentRepository> _mockCommentRepository;
        private Mock<IMessageBus> _mockMessageBus;
        private CommentUpdater _commentUpdater;
#pragma warning restore 0649

        [SetUp]
        public void SetUp()
        {
            MockHelper.InjectMocks(this);
            _commentUpdater = MockHelper.Create<CommentUpdater>(this);
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
                new CommentEntity
                {
                    CreatedAt = new DateTime(2017, 7, 31)
                }
            };

            _mockStoryMapper.Setup(p => p.ToCommentEntity(story.Comments[0], null, storyEntity))
                .Returns(commentEntities[0]);
            _mockCommentRepository.Setup(p => p.GetByCommentId(42))
                .Returns((CommentEntity) null);

            // act
            _commentUpdater.SaveComments(_mockSession.Object, story, storyEntity);

            // assert
            _mockSession.Verify(s => s.Save(commentEntities[0]));

            _mockSession.Verify(s => s.Save(It.Is<RecentActivityEntity>(
                r => r.Comment == commentEntities[0] && r.Story == storyEntity
                     && r.StoryVote == null && r.CreatedAt == new DateTime(2017, 7, 31)
            )));

            _mockMessageBus.Verify(m => m.Publish(commentEntities[0]));
        }
    }
}
