using System.Collections.Generic;
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
            
            var storyVoteEntities = new List<StoryVoteEntity>();

            // TODO make a friendlier DSL for setting up Restrictions in unit tests
            _mockStoryVoteCriteria.Setup(c => c.Add(It.Is<ICriterion>(crit => crit.ToString() == Restrictions.Eq("Story", storyEntity).ToString())))
                .Returns(_mockStoryVoteCriteria.Object);
            _mockStoryVoteCriteria.Setup(c => c.List<StoryVoteEntity>()).Returns(storyVoteEntities);
            
            // act
            IList<StoryVoteEntity> actualStoryVoteEntities = _storyVoteRepository.Get(_mockSession.Object, storyEntity);
            
            // assert
            Assert.AreEqual(storyVoteEntities, actualStoryVoteEntities);
        }
    }
}