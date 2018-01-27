using BuzzStats.DTOs;
using BuzzStats.Parsing.DTOs;
using BuzzStats.WebApi.DTOs;
using BuzzStats.WebApi.Storage;
using BuzzStats.WebApi.Storage.Entities;
using BuzzStats.WebApi.UnitTests.TestHelpers;
using Moq;
using NHibernate;
using NUnit.Framework;

namespace BuzzStats.WebApi.UnitTests.Storage
{
    [TestFixture]
    public class UpdaterTest
    {
#pragma warning disable 0649
        [MockBehavior(MockBehavior.Strict)]
        private Mock<ISession> _mockSession;

        [MockBehavior(MockBehavior.Strict)]
        private Mock<ITransaction> _mockTransaction;
        
        private Mock<IStoryUpdater> _mockStoryUpdater;
        private Mock<IStoryVoteUpdater> _mockStoryVoteUpdater;
        private Mock<ICommentUpdater> _mockCommentUpdater;
#pragma warning restore 0649
        private Updater _updater;

        [SetUp]
        public void SetUp()
        {
            MockHelper.InjectMocks(this);
            _mockTransaction.Setup(t => t.Dispose());
            _mockTransaction.Setup(t => t.Commit());
            _mockSession.Setup(s => s.BeginTransaction()).Returns(_mockTransaction.Object);
            _updater = MockHelper.Create<Updater>(this);
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

        [Test]
        public void Save_UsesStoryVoteUpdater()
        {
            // arrange
            var story = new Story
            {
                StoryId = 42
            };

            var storyEntity = new StoryEntity();

            _mockStoryUpdater.Setup(u => u.Save(_mockSession.Object, story)).Returns(storyEntity);

            // act
            _updater.Save(_mockSession.Object, story);

            // assert
            _mockStoryVoteUpdater.Verify(u => u.SaveStoryVotes(_mockSession.Object, story, storyEntity));
        }

        [Test]
        public void Save_UsesCommentUpdater()
        {
            // arrange
            var story = new Story
            {
                StoryId = 42
            };

            var storyEntity = new StoryEntity();

            _mockStoryUpdater.Setup(u => u.Save(_mockSession.Object, story)).Returns(storyEntity);

            // act
            _updater.Save(_mockSession.Object, story);

            // assert
            _mockCommentUpdater.Verify(u => u.SaveComments(_mockSession.Object, story, storyEntity));
        }

        [Test]
        public void Save_UsesTransaction()
        {
            var story = new Story
            {
                StoryId = 42
            };

            var storyEntity = new StoryEntity();

            _mockStoryUpdater.Setup(u => u.Save(_mockSession.Object, story)).Returns(storyEntity);

            // act
            _updater.Save(_mockSession.Object, story);

            // assert
            _mockSession.VerifyAll();
            _mockTransaction.VerifyAll();
        }
    }
}