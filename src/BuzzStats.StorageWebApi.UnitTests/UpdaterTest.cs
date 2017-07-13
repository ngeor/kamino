using BuzzStats.StorageWebApi.DTOs;
using Moq;
using NHibernate;
using NUnit.Framework;

namespace BuzzStats.StorageWebApi.UnitTests
{
    [TestFixture]
    public class UpdaterTest
    {
        private Mock<ISession> _mockSession;
        private Mock<StoryMapper> _mockStoryMapper;
        private Mock<IStoryUpdater> _mockStoryUpdater;
        private Updater _updater;

        [SetUp]
        public void SetUp()
        {
            _mockSession = new Mock<ISession>();
            _mockStoryMapper = new Mock<StoryMapper>();
            _mockStoryUpdater = new Mock<IStoryUpdater>();
            _updater = new Updater(_mockStoryMapper.Object, _mockStoryUpdater.Object);
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
    }
}