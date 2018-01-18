using BuzzStats.WebApi.Storage.Entities;
using BuzzStats.WebApi.Storage.Repositories;
using BuzzStats.WebApi.UnitTests.Storage.TestHelpers;
using BuzzStats.WebApi.UnitTests.TestHelpers;
using Moq;
using NHibernate;
using NUnit.Framework;

namespace BuzzStats.WebApi.UnitTests.Storage.Repositories
{
    [TestFixture]
    public class StoryRepositoryTest
    {
#pragma warning disable 0649
        private Mock<ISession> _mockSession;
        [MockBehavior(MockBehavior.Strict)] private Mock<ICriteria> _mockStoryCriteria;
#pragma warning restore 0649
        private StoryRepository _storyRepository;

        [SetUp]
        public void SetUp()
        {
            MockHelper.InjectMocks(this);
            _mockSession.Setup(s => s.CreateCriteria<StoryEntity>()).Returns(_mockStoryCriteria.Object);
            _storyRepository = MockHelper.Create<StoryRepository>(this);
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
            StoryEntity actualStoryEntity = _storyRepository.GetByStoryId(42);

            // assert
            Assert.AreEqual(storyEntity, actualStoryEntity);
        }
    }
}
