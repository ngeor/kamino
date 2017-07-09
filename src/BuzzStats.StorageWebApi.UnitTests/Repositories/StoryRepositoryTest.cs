using BuzzStats.StorageWebApi.Entities;
using BuzzStats.StorageWebApi.Repositories;
using Moq;
using NHibernate;
using NHibernate.Criterion;
using NUnit.Framework;

namespace BuzzStats.StorageWebApi.UnitTests.Repositories
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
            _mockStoryCriteria = new Mock<ICriteria>();
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

            _mockStoryCriteria.Setup(c => c.Add(It.Is<ICriterion>(crit => crit.ToString() == Restrictions.Eq("StoryId", 42).ToString())))
                .Returns(_mockStoryCriteria.Object);
            _mockStoryCriteria.Setup(c => c.UniqueResult<StoryEntity>()).Returns(storyEntity);
            
            // act
            StoryEntity actualStoryEntity = _storyRepository.GetByStoryId(_mockSession.Object, 42);
            
            // assert
            Assert.AreEqual(storyEntity, actualStoryEntity);
        }
    }
}