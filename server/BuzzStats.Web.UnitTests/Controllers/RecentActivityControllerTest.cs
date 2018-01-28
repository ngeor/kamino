using BuzzStats.Web.Controllers;
using BuzzStats.Web.Mongo;
using FluentAssertions;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;

namespace BuzzStats.Web.UnitTests.Controllers
{
    [TestClass]
    public class RecentActivityControllerTest
    {
        [TestMethod]
        public void Get()
        {
            var expectedResult = new[]
            {
                new RecentActivity()
            };

            var repositoryMock = new Mock<IRepository>();
            repositoryMock.Setup(p => p.GetRecentActivity())
                .ReturnsAsync(expectedResult);
            var recentActivityController = new RecentActivityController(repositoryMock.Object);

            // act
            var result = recentActivityController.Get().Result;

            // assert
            result.Should().Equal(expectedResult);
        }
    }
}
