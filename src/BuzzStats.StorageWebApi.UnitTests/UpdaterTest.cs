using BuzzStats.StorageWebApi.DTOs;
using BuzzStats.StorageWebApi.Entities;
using NHibernate;
using NUnit.Framework;
using Moq;

namespace BuzzStats.StorageWebApi.UnitTests
{
    [TestFixture]
    public class UpdaterTest
    {
        private Mock<ISession> _mockSession;
        private Mock<StoryMapper> _mockStoryMapper;
        private Updater _updater;

        [SetUp]
        public void SetUp()
        {
            _mockSession = new Mock<ISession>();
            _mockStoryMapper = new Mock<StoryMapper>();
            _updater = new Updater(_mockStoryMapper.Object);
        }

        [Test]
        public void Save()
        {
            // arrange
            var story = new Story
            {
                StoryId = 42
            };

            var storyEntity = new StoryEntity
            {
                StoryId = 42
            };

            _mockStoryMapper.Setup(m => m.ToStoryEntity(story)).Returns(storyEntity);

            // act
            _updater.Save(_mockSession.Object, story);

            // assert
            _mockSession.Verify(s => s.SaveOrUpdate(storyEntity));
        }
    }
}