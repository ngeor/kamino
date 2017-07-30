using System.Collections.Generic;
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
    public class StoryVoteUpdaterTest
    {
        private Mock<ISession> _mockSession;
        private Mock<StoryMapper> _mockStoryMapper;
        private Mock<IMessageBus> _mockMessageBus;
        
        [MockBehavior(MockBehavior.Strict)]
        private Mock<StoryVoteRepository> _mockStoryVoteRepository;
        
        private StoryVoteUpdater _storyVoteUpdater;

        [SetUp]
        public void SetUp()
        {
            MockHelper.InjectMocks(this);
            _storyVoteUpdater = MockHelper.Create<StoryVoteUpdater>(this);
        }

        [Test]
        public void SaveStoryVotes_WhenNoVotesExist_SavesAllVotes()
        {
            // arrange
            var story = new Story();
            var storyEntity = new StoryEntity();
            var storyVoteEntities = new[]
            {
                new StoryVoteEntity()
            };

            _mockStoryMapper.Setup(m => m.ToStoryVoteEntities(story, storyEntity))
                .Returns(storyVoteEntities);
            _mockStoryVoteRepository.Setup(r => r.Get(_mockSession.Object, storyEntity))
                .Returns(new List<StoryVoteEntity>());

            // act
            _storyVoteUpdater.SaveStoryVotes(_mockSession.Object, story, storyEntity);

            // assert
            _mockSession.Verify(s => s.Save(storyVoteEntities[0]));
            _mockMessageBus.Verify(m => m.Publish(storyVoteEntities[0]));
        }

        [Test]
        public void SaveStoryVotes_WhenVoteExist_DoesNotInsertNewVote()
        {
            // arrange
            var story = new Story();
            var storyEntity = new StoryEntity();
            var storyVoteEntities = new[]
            {
                new StoryVoteEntity
                {
                    Username = "user"
                }
            };

            _mockStoryMapper.Setup(m => m.ToStoryVoteEntities(story, storyEntity))
                .Returns(storyVoteEntities);
            _mockStoryVoteRepository.Setup(r => r.Get(_mockSession.Object, storyEntity)).Returns(
                new List<StoryVoteEntity>
                {
                    new StoryVoteEntity
                    {
                        Username = "user"
                    }
                });

            // act
            _storyVoteUpdater.SaveStoryVotes(_mockSession.Object, story, storyEntity);

            // assert
            _mockSession.Verify(s => s.SaveOrUpdate(It.IsAny<StoryVoteEntity>()), Times.Never);
            _mockMessageBus.Verify(m => m.Publish(It.IsAny<StoryVoteEntity>()), Times.Never);
        }

        [Test]
        public void SaveStoryVotes_WhenVoteNoLongerExists_DeletesVote()
        {
            // arrange
            var story = new Story();
            var storyEntity = new StoryEntity();
            var existingStoryVoteEntity = new StoryVoteEntity
            {
                Username = "user"
            };

            _mockStoryMapper.Setup(m => m.ToStoryVoteEntities(story, storyEntity))
                .Returns(new StoryVoteEntity[0]);
            _mockStoryVoteRepository.Setup(r => r.Get(_mockSession.Object, storyEntity)).Returns(
                new List<StoryVoteEntity>
                {
                    existingStoryVoteEntity
                });

            // act
            _storyVoteUpdater.SaveStoryVotes(_mockSession.Object, story, storyEntity);

            // assert
            _mockSession.Verify(s => s.Delete(existingStoryVoteEntity));
            _mockMessageBus.Verify(m => m.Publish(It.IsAny<StoryVoteEntity>()), Times.Never);
        }
    }
}