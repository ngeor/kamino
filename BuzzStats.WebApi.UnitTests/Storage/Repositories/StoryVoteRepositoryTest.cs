using System.Collections.Generic;
using BuzzStats.StorageWebApi.Entities;
using BuzzStats.StorageWebApi.Repositories;
using BuzzStats.StorageWebApi.UnitTests.TestHelpers;
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
            _mockStoryVoteCriteria = new Mock<ICriteria>(MockBehavior.Strict);
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
            _mockStoryVoteCriteria.SetupEq("Story", storyEntity);
            _mockStoryVoteCriteria.Setup(c => c.List<StoryVoteEntity>()).Returns(storyVoteEntities);
            
            // act
            IList<StoryVoteEntity> actualStoryVoteEntities = _storyVoteRepository.Get(_mockSession.Object, storyEntity);
            
            // assert
            Assert.AreEqual(storyVoteEntities, actualStoryVoteEntities);
        }
    }
}