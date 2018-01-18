using BuzzStats.WebApi.Storage.Session;
using BuzzStats.WebApi.UnitTests.TestHelpers;
using Moq;
using NHibernate;
using NUnit.Framework;

namespace BuzzStats.WebApi.UnitTests.Storage.Session
{
    [TestFixture]
    public class LazySessionTest
    {
#pragma warning disable 0649
        private Mock<ISession> _mockSession;
        private Mock<ISessionManager> _mockSessionManager;
#pragma warning restore 0649
        private LazySession _lazySession;

        [SetUp]
        public void SetUp()
        {
            MockHelper.InjectMocks(this);
            _mockSessionManager.SetupGet(m => m.Session).Returns(_mockSession.Object);
            _lazySession = new LazySession(_mockSessionManager.Object);
        }

        [Test]
        public void Dispose()
        {
            _lazySession.Dispose();
            _mockSession.Verify(s => s.Dispose());
        }

        [Test]
        public void Flush()
        {
            _lazySession.Flush();
            _mockSession.Verify(s => s.Flush());
        }

        [Test]
        public void Save()
        {
            _mockSession.Setup(s => s.Save("hello")).Returns("hi");
            var result = _lazySession.Save("hello");
            Assert.AreEqual("hi", result);
        }

        [Test]
        public void Update()
        {
            _lazySession.Update("hi");
            _mockSession.Verify(s => s.Update("hi"));
        }

        [Test]
        public void CreateCriteria()
        {
            // arrange
            ICriteria criteria = Mock.Of<ICriteria>();
            _mockSession.Setup(s => s.CreateCriteria<string>()).Returns(criteria);

            // act
            var result = _lazySession.CreateCriteria<string>();

            // assert
            Assert.AreEqual(criteria, result);
        }
    }
}
