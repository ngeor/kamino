using BuzzStats.StorageWebApi.Entities;
using BuzzStats.StorageWebApi.Repositories;
using Moq;
using NHibernate;
using NHibernate.Criterion;
using NUnit.Framework;

namespace BuzzStats.StorageWebApi.UnitTests.Repositories
{
    [TestFixture]
    public class StoryVoteRepositoryTest
    {
        private Mock<ISession> _mockSession;
        private Mock<ICriteria> _mockStoryVoteCriteria;
        private StoryVoteRepository _storyVoteRepository;

        [SetUp]
        public void SetUp()
        {
            _mockSession = new Mock<ISession>();
            _mockStoryVoteCriteria = new Mock<ICriteria>();
            _mockSession.Setup(s => s.CreateCriteria<StoryVoteEntity>()).Returns(_mockStoryVoteCriteria.Object);
            _storyVoteRepository = new StoryVoteRepository();
        }
        
        [Test]
        public void Get()
        {
            // arrange
            var storyEntity = new StoryEntity
            {
                StoryId = 42
            };
            
            var storyVoteEntity = new StoryVoteEntity();

            // TODO make a friendlier DSL for setting up Restrictions in unit tests
            _mockStoryVoteCriteria.Setup(c => c.Add(It.Is<ICriterion>(crit => crit.ToString() == Restrictions.Eq("Story", storyEntity).ToString())))
                .Returns(_mockStoryVoteCriteria.Object);
            _mockStoryVoteCriteria.Setup(c => c.Add(It.Is<ICriterion>(crit => crit.ToString() == Restrictions.Eq("Username", "voter").ToString())))
                .Returns(_mockStoryVoteCriteria.Object);
            _mockStoryVoteCriteria.Setup(c => c.UniqueResult<StoryVoteEntity>()).Returns(storyVoteEntity);
            
            // act
            StoryVoteEntity actualStoryVoteEntity = _storyVoteRepository.Get(_mockSession.Object, storyEntity, "voter");
            
            // assert
            Assert.AreEqual(storyVoteEntity, actualStoryVoteEntity);
        }
    }
}