using BuzzStats.StorageWebApi.DTOs;
using BuzzStats.StorageWebApi.Entities;
using Moq;
using NHibernate;
using NUnit.Framework;

namespace BuzzStats.StorageWebApi.UnitTests
{
    [TestFixture]
    public class UpdaterTest
    {
        private Mock<ISession> _mockSession;
        private Mock<IStoryUpdater> _mockStoryUpdater;
        private Mock<IStoryVoteUpdater> _mockStoryVoteUpdater;
        private Updater _updater;

        [SetUp]
        public void SetUp()
        {
            _mockSession = new Mock<ISession>();
            _mockStoryUpdater = new Mock<IStoryUpdater>();
            _mockStoryVoteUpdater = new Mock<IStoryVoteUpdater>();
            _updater = new Updater(_mockStoryUpdater.Object, _mockStoryVoteUpdater.Object);
        }

        [Test]
        public void Save_UsesStoryUpdater()
        {
            // arrange
            var story = new Story
            {
                StoryId = 42
            };

            // act
            _updater.Save(_mockSession.Object, story);

            // assert
            _mockStoryUpdater.Verify(u => u.Save(_mockSession.Object, story));
        }

        [Test]
        public void Save_UsesStoryVoteUpdater()
        {
            // arrange
            var story = new Story
            {
                StoryId = 42
            };

            var storyEntity = new StoryEntity();

            _mockStoryUpdater.Setup(u => u.Save(_mockSession.Object, story)).Returns(storyEntity);

            // act
            _updater.Save(_mockSession.Object, story);

            // assert
            _mockStoryVoteUpdater.Verify(u => u.SaveStoryVotes(_mockSession.Object, story, storyEntity));
        }
    }
}