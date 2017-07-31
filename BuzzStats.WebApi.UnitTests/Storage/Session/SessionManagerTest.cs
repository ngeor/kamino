using System;
using BuzzStats.WebApi.Storage.Session;
using BuzzStats.WebApi.UnitTests.TestHelpers;
using Castle.DynamicProxy;
using Moq;
using NHibernate;
using NUnit.Framework;

namespace BuzzStats.WebApi.UnitTests.Storage.Session
{
    [TestFixture]
    public class SessionManagerTest
    {
#pragma warning disable 0649
        private Mock<ISessionFactory> _mockSessionFactory;
        private Mock<ISession> _mockRealSession;
#pragma warning restore 0649
        private SessionManager _sessionManager;

        [SetUp]
        public void SetUp()
        {
            MockHelper.InjectMocks(this);
            _mockSessionFactory.Setup(f => f.OpenSession()).Returns(_mockRealSession.Object);
            _sessionManager = MockHelper.Create<SessionManager>(this);
        }

        [Test]
        public void Session_WhenNotSet_IsNull()
        {
            Assert.IsNull(_sessionManager.Session);
        }

        [Test]
        public void Create_WhenNotSet_RegistersSession()
        {
            // act
            var result = _sessionManager.Create();

            // assert
            Assert.IsNotNull(result);
            Assert.AreEqual(result, _sessionManager.Session);
            Assert.AreEqual(_mockRealSession.Object, ProxyUtil.GetUnproxiedInstance(result));
        }

        [Test]
        public void Create_WhenAlreadySet_ThrowsException()
        {
            // arrange
            _sessionManager.Create();

            // act & assert
            Assert.Throws<InvalidOperationException>(() => { _sessionManager.Create(); });
        }

        [Test]
        public void Dispose_ClearsSession()
        {
            // arrange
            var session = _sessionManager.Create();

            // act
            session.Dispose();

            // assert
            Assert.IsNull(_sessionManager.Session);
            _mockRealSession.Verify(s => s.Dispose());
        }
    }
}
