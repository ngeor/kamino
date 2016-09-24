using System;
using NUnit.Framework;
using BuzzStats.Boot;
using BuzzStats.Boot.Web;
using BuzzStats.Data;
using BuzzStats.Services;

namespace BuzzStats.Tests.Boot.Web
{
    [TestFixture]
    [Category("Integration")]
    public class ApplicationTest
    {
        private IServiceProvider _resolver;

        [TestFixtureSetUp]
        public void TestFixtureSetUp()
        {
            _resolver = Application.Boot();
        }

        [TestFixtureTearDown]
        public void TestFixtureTearDown()
        {
            Application.ShutDown();
        }

        [Test]
        public void ShouldHaveIApiService()
        {
            var apiService = _resolver.GetService(typeof(IApiService)) as IApiService;
            Assert.IsNotNull(apiService);
        }

        [Test]
        public void ShouldHaveDiagnosticsService()
        {
            var service = _resolver.GetService(typeof(IDiagnosticsService)) as IDiagnosticsService;
            Assert.IsInstanceOf(typeof (DiagnosticsServiceClient), service);
        }

        [Test]
        public void ShouldHaveRecentActivityService()
        {
            var service = _resolver.GetService(typeof(IRecentActivityService)) as IRecentActivityService;
            Assert.IsInstanceOf(typeof (RecentActivityServiceClient), service);
        }
    }
}
