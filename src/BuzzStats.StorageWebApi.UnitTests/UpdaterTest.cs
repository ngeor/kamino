using BuzzStats.StorageWebApi.DTOs;
using BuzzStats.StorageWebApi.Entities;
using BuzzStats.StorageWebApi.Repositories;
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
        private Mock<StoryRepository> _mockStoryRepository;
        private Updater _updater;

        [SetUp]
        public void SetUp()
        {
            _mockSession = new Mock<ISession>();
            _mockStoryMapper = new Mock<StoryMapper>();
            _mockStoryRepository = new Mock<StoryRepository>();
            _updater = new Updater(_mockStoryMapper.Object, _mockStoryRepository.Object);
        }

        [Test]
        public void Save_WhenStoryIsNew()
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
            _mockStoryRepository.Setup(r => r.GetByStoryId(_mockSession.Object, 42)).Returns((StoryEntity)null);

            // act
            _updater.Save(_mockSession.Object, story);

            // assert
            _mockSession.Verify(s => s.Save(storyEntity));
        }
        
        [Test]
        public void Save_WhenStoryExists()
        {
            // arrange
            var story = new Story
            {
                StoryId = 42
            };

            var existingStory = new StoryEntity
            {

            };
                
            var storyEntity = new StoryEntity
            {
                StoryId = 42
            };

            _mockStoryMapper.Setup(m => m.ToStoryEntity(story)).Returns(storyEntity);
            _mockStoryRepository.Setup(r => r.GetByStoryId(_mockSession.Object, 42)).Returns(existingStory);

            // act
            _updater.Save(_mockSession.Object, story);

            // assert
            _mockSession.Verify(s => s.SaveOrUpdate(storyEntity));
        }
    }
}