using System;
using Moq;
using NUnit.Framework;
using BuzzStats.Crawl;
using BuzzStats.Data;

namespace BuzzStats.Tests.Crawl
{
    [TestFixture]
    public class WarmCacheTest
    {
        [Test]
        public void ShouldContinueAfterExceptions()
        {
            IApiService apiService = Mock.Of<IApiService>();
            Mock.Get(apiService)
                .Setup(p => p.GetRecentActivity(It.IsAny<RecentActivityRequest>()))
                .Throws<InvalidOperationException>();
            WarmCache cache = new WarmCache(apiService);
            cache.UpdateRecentActivity();
            Mock.Get(apiService).VerifyAll();
        }
    }
}