using BuzzStats.StorageWebApi.DTOs;
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
            _mockStoryVoteRepository = new Mock<StoryVoteRepository>();
            _storyVoteUpdater = new StoryVoteUpdater(_mockStoryMapper.Object, _mockStoryVoteRepository.Object);
        }

        [Test]
        public void SaveStoryVotes_SavesAllVotes()
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

            // act
            _storyVoteUpdater.SaveStoryVotes(_mockSession.Object, story, storyEntity);

            // assert
            _mockSession.Verify(s => s.SaveOrUpdate(storyVoteEntities[0]));
        }
    }
}