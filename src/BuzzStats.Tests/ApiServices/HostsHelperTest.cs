using System;
using System.Collections.Generic;
using NUnit.Framework;
using NGSoftware.Common;
using BuzzStats.Data;

namespace BuzzStats.Tests.ApiServices
{
    partial class ApiServiceTest
    {
        [Test]
        public void TestNullFilter()
        {
            Assert.Throws<ArgumentNullException>(() => ApiService.GetHostStats(null));
        }

        [Test]
        public void TestNegativeMaxResults()
        {
            Assert.Throws<ArgumentOutOfRangeException>(
                () => ApiService.GetHostStats(new HostStatsRequest {MaxResults = -1}));
        }

        [Test]
        public void TestNoFilter()
        {
            MockStoryDataLayer.Setup(p => p.GetStoryCountsPerHost(DateRange.Empty)).Returns(new Dictionary<string, int>
            {
                {"ngeor.net", 10}
            });

            MockStoryVoteDataLayer.Setup(p => p.SumPerHost(DateRange.Empty)).Returns(new Dictionary<string, int>
            {
                {"ngeor.net", 100},
                {"youtube.com", 5}
            });

            HostStats[] hostStats = ApiService.GetHostStats(new HostStatsRequest());
            Assert.IsNotNull(hostStats);
            Assert.AreEqual(2, hostStats.Length);
            Assert.AreEqual(new HostStats {Host = "ngeor.net", StoryCount = 10, VoteCount = 100}, hostStats[0]);
            Assert.AreEqual(new HostStats {Host = "youtube.com", StoryCount = 0, VoteCount = 5}, hostStats[1]);
        }

        [Test]
        public void TestSortByVoteStoryRatio()
        {
            MockStoryDataLayer.Setup(p => p.GetStoryCountsPerHost(DateRange.Empty)).Returns(new Dictionary<string, int>
            {
                {"ngeor.net", 10},
                {"youtube.com", 10}
            });

            MockStoryVoteDataLayer.Setup(p => p.SumPerHost(DateRange.Empty)).Returns(new Dictionary<string, int>
            {
                {"ngeor.net", 20},
                {"youtube.com", 40}
            });

            HostStats[] hostStats =
                ApiService.GetHostStats(new HostStatsRequest {SortExpression = "VoteStoryRatio DESC"});
            Assert.IsNotNull(hostStats);
            Assert.AreEqual(2, hostStats.Length);
            Assert.AreEqual(new HostStats {Host = "youtube.com", StoryCount = 10, VoteCount = 40}, hostStats[0]);
            Assert.AreEqual(new HostStats {Host = "ngeor.net", StoryCount = 10, VoteCount = 20}, hostStats[1]);
        }
    }
}
