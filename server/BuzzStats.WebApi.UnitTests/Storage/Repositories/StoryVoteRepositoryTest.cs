using System.Collections.Generic;
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
    public class StoryVoteRepositoryTest
    {
#pragma warning disable 0649
        private Mock<ISession> _mockSession;
        [MockBehavior(MockBehavior.Strict)]
        private Mock<ICriteria> _mockStoryVoteCriteria;
#pragma warning restore 0649
        private StoryVoteRepository _storyVoteRepository;

        [SetUp]
        public void SetUp()
        {
            MockHelper.InjectMocks(this);
            _mockSession.Setup(s => s.CreateCriteria<StoryVoteEntity>()).Returns(_mockStoryVoteCriteria.Object);
            _storyVoteRepository = MockHelper.Create<StoryVoteRepository>(this);
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
            IList<StoryVoteEntity> actualStoryVoteEntities = _storyVoteRepository.Get(storyEntity);

            // assert
            Assert.AreEqual(storyVoteEntities, actualStoryVoteEntities);
        }
    }
}
