using System;
using BuzzStats.Boot.Web;
using BuzzStats.Data;
using BuzzStats.Services;
using NUnit.Framework;

namespace BuzzStats.UnitTests.Boot.Web
{
    [TestFixture]
    [Category("Integration")]
    public class ApplicationTest
    {
        private IServiceProvider _resolver;

        [OneTimeSetUp]
        public void TestFixtureSetUp()
        {
            _resolver = Application.Boot();
        }

        [OneTimeTearDown]
        public void TestFixtureTearDown()
        {
            Application.ShutDown();
        }

        [Test]
        public void ShouldHaveDiagnosticsService()
        {
            var service = _resolver.GetService(typeof(IDiagnosticsService)) as IDiagnosticsService;
            Assert.IsInstanceOf(typeof(DiagnosticsServiceClient), service);
        }

        [Test]
        public void ShouldHaveRecentActivityService()
        {
            var service = _resolver.GetService(typeof(IRecentActivityService)) as IRecentActivityService;
            Assert.IsInstanceOf(typeof(RecentActivityServiceClient), service);
        }
    }
}