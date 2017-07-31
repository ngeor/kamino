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
    public class RecentActivityRepositoryTest
    {
#pragma warning disable 0649
        private Mock<ISession> _mockSession;

        [MockBehavior(MockBehavior.Strict)]
        private Mock<ICriteria> _mockRecentActivityCriteria;
        private RecentActivityRepository _recentActivityRepository;
#pragma warning restore 0649

        [SetUp]
        public void SetUp()
        {
            MockHelper.InjectMocks(this);
            _mockSession.Setup(s => s.CreateCriteria<RecentActivityEntity>()).Returns(_mockRecentActivityCriteria.Object);
            _recentActivityRepository = MockHelper.Create<RecentActivityRepository>(this);
        }

        [Test]
        public void Get()
        {
            // arrange
            var expected = new List<RecentActivityEntity>();
            _mockRecentActivityCriteria.SetupOrderDesc("CreatedAt");
            _mockRecentActivityCriteria.SetupOrderDesc("Id");
            _mockRecentActivityCriteria.Setup(c => c.SetMaxResults(20)).Returns(_mockRecentActivityCriteria.Object);
            _mockRecentActivityCriteria.Setup(c => c.List<RecentActivityEntity>()).Returns(expected);

            // act
            var result = _recentActivityRepository.Get(_mockSession.Object);

            // assert
            Assert.AreEqual(expected, result);
        }
    }
}
