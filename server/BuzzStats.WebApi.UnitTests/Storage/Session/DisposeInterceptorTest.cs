using BuzzStats.WebApi.Storage.Session;
using BuzzStats.WebApi.UnitTests.TestHelpers;
using Moq;
using NHibernate;
using NUnit.Framework;

namespace BuzzStats.WebApi.UnitTests.Storage.Session
{
    [TestFixture]
    public class DisposeInterceptorTest
    {
#pragma warning disable 0649
        private Mock<ISession> _mockSession;
#pragma warning restore 0649
        private ISession _decorated;
        private bool _disposeActionCalled;

        [SetUp]
        public void SetUp()
        {
            MockHelper.InjectMocks(this);
            _disposeActionCalled = false;
            _decorated = DisposeInterceptor.Decorate(_mockSession.Object, () =>
            {
                _disposeActionCalled = true;
            });
        }

        [Test]
        public void Clear_CallsRealSession()
        {
            // act
            _decorated.Clear();

            // assert
            _mockSession.Verify(s => s.Clear());
            Assert.IsFalse(_disposeActionCalled);
        }

        [Test]
        public void CacheMode_CallsRealSession()
        {
            // arrange
            _mockSession.SetupGet(s => s.CacheMode).Returns(CacheMode.Refresh);

            // act
            var result = _decorated.CacheMode;

            // assert
            Assert.AreEqual(CacheMode.Refresh, result);
            Assert.IsFalse(_disposeActionCalled);
        }

        [Test]
        public void Dispose_CallsDisposeAction()
        {
            // act
            _decorated.Dispose();

            // assert
            _mockSession.Verify(s => s.Dispose());
            Assert.IsTrue(_disposeActionCalled);
        }
    }
}
