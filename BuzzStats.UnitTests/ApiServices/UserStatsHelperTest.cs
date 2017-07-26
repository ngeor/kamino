using System;
using System.Collections.Generic;
using BuzzStats.Data;
using NGSoftware.Common;
using NUnit.Framework;

namespace BuzzStats.UnitTests.ApiServices
{
    partial class ApiServiceTest
    {
        /// <summary>
        /// Tests the <see cref="UserStatsHelper.Get"/> method.
        /// </summary>
        [Test]
        public void TestUserStats()
        {
            MockStoryDataLayer
                .Setup(p => p.GetStoryCountsPerUser(
                    DateRange.Create(new DateTime(2010, 1, 1), new DateTime(2010, 4, 1))))
                .Returns(new Dictionary<string, int>
                {
                    {"nikolaos", 2},
                    {"test", 4}
                });

            MockStoryDataLayer
                .Setup(p => p.GetCommentedStoryCountsPerUser(
                    DateRange.Create(new DateTime(2010, 1, 1), new DateTime(2010, 4, 1))))
                .Returns(new Dictionary<string, int>
                {
                    {"nikolaos", 3}
                });

            MockCommentDataLayer
                .Setup(p => p.CountBuriedPerUser(
                    DateRange.Create(new DateTime(2010, 1, 1), new DateTime(2010, 4, 1))))
                .Returns(new Dictionary<string, int>
                {
                    {"nikolaos", 1}
                });

            MockCommentDataLayer
                .Setup(p => p.CountPerUser(
                    DateRange.Create(new DateTime(2010, 1, 1), new DateTime(2010, 4, 1))))
                .Returns(new Dictionary<string, int>
                {
                    {"nikolaos", 10}
                });

            MockCommentDataLayer
                .Setup(p => p.SumVotesDownPerUser(
                    DateRange.Create(new DateTime(2010, 1, 1), new DateTime(2010, 4, 1))))
                .Returns(new Dictionary<string, int>
                {
                    {"nikolaos", 5}
                });

            MockCommentDataLayer
                .Setup(p => p.SumVotesUpPerUser(
                    DateRange.Create(new DateTime(2010, 1, 1), new DateTime(2010, 4, 1))))
                .Returns(new Dictionary<string, int>
                {
                    {"nikolaos", 4}
                });

            UserStats[] userStats = ApiService.GetUserStats(new UserStatsRequest
            {
                Start = new DateTime(2010, 1, 1),
                Stop = new DateTime(2010, 4, 1)
            });

            Assert.IsNotNull(userStats);
            Assert.AreEqual(3, userStats.Length);

            Assert.AreEqual(
                new UserStats
                {
                    Username = string.Empty,
                    BuriedCommentCount = 0.5,
                    CommentCount = 5,
                    CommentedStoriesCount = 1.5,
                    VotesDown = 2.5,
                    VotesUp = 2,
                    StoryCount = 3
                },
                userStats[0]);

            Assert.AreEqual(
                new UserStats
                {
                    Username = "nikolaos",
                    BuriedCommentCount = 1,
                    CommentCount = 10,
                    CommentedStoriesCount = 3,
                    VotesDown = 5,
                    VotesUp = 4,
                    StoryCount = 2
                },
                userStats[1]);

            Assert.AreEqual(
                new UserStats
                {
                    Username = "test",
                    BuriedCommentCount = 0,
                    CommentCount = 0,
                    CommentedStoriesCount = 0,
                    VotesDown = 0,
                    VotesUp = 0,
                    StoryCount = 4
                },
                userStats[2]);
        }
    }
}