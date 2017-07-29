using BuzzStats.WebApi.Storage.Entities;
using BuzzStats.WebApi.Storage.Repositories;
using BuzzStats.WebApi.UnitTests.Storage.TestHelpers;
using Moq;
using NHibernate;
using NUnit.Framework;

namespace BuzzStats.WebApi.UnitTests.Storage.Repositories
{
    [TestFixture]
    public class StoryRepositoryTest
    {
        private Mock<ISession> _mockSession;
        private Mock<ICriteria> _mockStoryCriteria;
        private StoryRepository _storyRepository;

        [SetUp]
        public void SetUp()
        {
            _mockSession = new Mock<ISession>();
            _mockStoryCriteria = new Mock<ICriteria>(MockBehavior.Strict);
            _mockSession.Setup(s => s.CreateCriteria<StoryEntity>()).Returns(_mockStoryCriteria.Object);
            _storyRepository = new StoryRepository();
        }
        
        [Test]
        public void GetByStoryId()
        {
            // arrange
            var storyEntity = new StoryEntity
            {
                StoryId = 42
            };

            _mockStoryCriteria.SetupEq("StoryId", 42);
            _mockStoryCriteria.Setup(c => c.UniqueResult<StoryEntity>()).Returns(storyEntity);
            
            // act
            StoryEntity actualStoryEntity = _storyRepository.GetByStoryId(_mockSession.Object, 42);
            
            // assert
            Assert.AreEqual(storyEntity, actualStoryEntity);
        }
    }
}