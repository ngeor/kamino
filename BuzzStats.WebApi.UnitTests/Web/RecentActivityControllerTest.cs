using System.Collections.Generic;
using BuzzStats.WebApi.DTOs;
using BuzzStats.WebApi.Storage;
using BuzzStats.WebApi.UnitTests.TestHelpers;
using BuzzStats.WebApi.Web;
using Moq;
using NUnit.Framework;

namespace BuzzStats.WebApi.UnitTests.Web
{
    [TestFixture]
    public class RecentActivityControllerTest
    {
#pragma warning disable 0649
        private Mock<IStorageClient> _mockStorageClient;
#pragma warning restore 0649
        private RecentActivityController _recentActivityController;

        [SetUp]
        public void SetUp()
        {
            MockHelper.InjectMocks(this);
            _recentActivityController = MockHelper.Create<RecentActivityController>(this);
        }

        [Test]
        public void Get()
        {
            // arrange
            var recentActivities = new List<RecentActivity>();
            _mockStorageClient.Setup(s => s.GetRecentActivity())
                .Returns(recentActivities);

            // act
            var result = _recentActivityController.Get();

            // assert
            Assert.AreEqual(recentActivities, result);
        }
    }
}
