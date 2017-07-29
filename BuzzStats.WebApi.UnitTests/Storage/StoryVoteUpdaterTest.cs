using System.Collections.Generic;
using BuzzStats.CrawlerService.DTOs;
using BuzzStats.StorageWebApi.Entities;
using BuzzStats.StorageWebApi.Repositories;
using Moq;
using NHibernate;
using NUnit.Framework;

namespace BuzzStats.StorageWebApi.UnitTests
{
    [TestFixture]
    public class StoryVoteUpdaterTest
    {
        private Mock<ISession> _mockSession;
        private Mock<StoryMapper> _mockStoryMapper;
        private Mock<StoryVoteRepository> _mockStoryVoteRepository;
        private StoryVoteUpdater _storyVoteUpdater;

        [SetUp]
        public void SetUp()
        {
            _mockSession = new Mock<ISession>();
            _mockStoryMapper = new Mock<StoryMapper>();
            _mockStoryVoteRepository = new Mock<StoryVoteRepository>(MockBehavior.Strict);
            _storyVoteUpdater = new StoryVoteUpdater(_mockStoryMapper.Object, _mockStoryVoteRepository.Object);
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
        }
    }
}