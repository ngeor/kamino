using BuzzStats.WebApi.Storage.Entities;
using BuzzStats.WebApi.Storage.Repositories;
using BuzzStats.WebApi.Storage.Session;
using BuzzStats.WebApi.UnitTests.TestHelpers;
using Moq;
using NHibernate;
using NUnit.Framework;

namespace BuzzStats.WebApi.UnitTests.Storage.Session
{
    [TestFixture]
    public class RepositoryInterceptorTest
    {
#pragma warning disable 0649
        private Mock<IStoryRepository> _mockRealRepository;
        private Mock<ISessionManager> _mockSessionManager;
#pragma warning restore 0649

        private IStoryRepository _decoratedRepository;

        [SetUp]
        public void SetUp()
        {
            MockHelper.InjectMocks(this);
            _decoratedRepository =
                RepositoryInterceptor.Decorate(
                    _mockRealRepository.Object,
                    _mockSessionManager.Object);
        }

        [Test]
        public void WhenSessionExists_NoNewSessionIsOpened()
        {
            // arrange
            var storyEntity = new StoryEntity();
            _mockSessionManager.SetupGet(m => m.Session).Returns(Mock.Of<ISession>());
            _mockRealRepository.Setup(r => r.GetByStoryId(42)).Returns(storyEntity);

            // act
            var result = _decoratedRepository.GetByStoryId(42);

            // assert
            Assert.AreEqual(storyEntity, result);
            _mockSessionManager.Verify(m => m.Create(), Times.Never);
        }

        [Test]
        public void WhenNoSessionExists_NewSessionIsOpened()
        {
            // arrange
            var storyEntity = new StoryEntity();
            _mockRealRepository.Setup(r => r.GetByStoryId(42)).Returns(storyEntity);
            var mockSession = new Mock<ISession>();
            _mockSessionManager.Setup(m => m.Create()).Returns(mockSession.Object);

            // act
            var result = _decoratedRepository.GetByStoryId(42);

            // assert
            Assert.AreEqual(storyEntity, result);
            mockSession.Verify(s => s.Dispose());
        }
    }
}
