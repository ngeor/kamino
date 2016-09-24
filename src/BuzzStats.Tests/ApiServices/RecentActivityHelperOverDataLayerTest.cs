// --------------------------------------------------------------------------------
// <copyright file="RecentActivityHelperOverDataLayerTest.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/07/04
// * Time: 9:35 πμ
// --------------------------------------------------------------------------------

using System;
using Moq;
using NUnit.Framework;
using NGSoftware.Common;
using BuzzStats.Data;

namespace BuzzStats.Tests.ApiServices
{
    partial class ApiServiceTest
    {
        /// <summary>
        /// Tests the <see cref="RecentActivityHelperOverDataLayer.Take"/> method.
        /// </summary>
        [Test]
        public void TestTake()
        {
            Mock<IStoryQuery> mockStoryQuery = new Mock<IStoryQuery>(MockBehavior.Strict);
            MockStoryDataLayer.Setup(p => p.Query()).Returns(mockStoryQuery.Object);
            mockStoryQuery.Setup(p => p.Username(null)).Returns(mockStoryQuery.Object);
            mockStoryQuery.Setup(p => p.Take(10)).Returns(mockStoryQuery.Object);
            mockStoryQuery.Setup(p => p.AsEnumerable()).Returns(new[]
            {
                new StoryData
                {
                    StoryId = 42,
                    Username = "someone",
                    CreatedAt = TestableDateTime.UtcNow.Subtract(TimeSpan.FromMinutes(100)),
                    DetectedAt = TestableDateTime.UtcNow - TimeSpan.FromMinutes(15),
                    Title = "my story"
                }
            });

            MockStoryVoteDataLayer.Setup(p => p.Query(10, null))
                .Returns(new[]
                {
                    new StoryVoteData
                    {
                        Story = new StoryData
                        {
                            StoryId = 42,
                            Title = "my story"
                        },
                        CreatedAt = TestableDateTime.UtcNow.Subtract(TimeSpan.FromMinutes(60)),
                        Username = "another"
                    }
                });

            MockCommentDataLayer.Setup(p => p.Query(new CommentDataQueryParameters
            {
                Count = 10,
                SortBy = new[] {CommentSortField.CreatedAt.Desc()}
            }))
                .Returns(new[]
                {
                    new CommentData
                    {
                        CommentId = 114,
                        Story = new StoryData
                        {
                            StoryId = 42,
                            Title = "my story"
                        },
                        CreatedAt = TestableDateTime.UtcNow.Subtract(TimeSpan.FromMinutes(80)),
                        DetectedAt = TestableDateTime.UtcNow - TimeSpan.FromMinutes(20),
                        Username = "nikolaos"
                    }
                });

            RecentActivity[] recentActivity = ApiService.GetRecentActivity(null);
            Assert.IsNotNull(recentActivity);
            Assert.AreEqual(3, recentActivity.Length);
            Assert.AreEqual(
                new RecentActivity
                {
                    What = RecentActivityKind.NewStoryVote,
                    Who = "another",
                    Age = TimeSpan.FromHours(1),
                    DetectedAtAge = TimeSpan.FromHours(1),
                    StoryId = 42,
                    StoryTitle = "my story"
                },
                recentActivity[0]);

            Assert.AreEqual(
                new RecentActivity
                {
                    CommentId = 114,
                    What = RecentActivityKind.NewComment,
                    Who = "nikolaos",
                    Age = TimeSpan.FromMinutes(80),
                    DetectedAtAge = TimeSpan.FromMinutes(20),
                    StoryId = 42,
                    StoryTitle = "my story"
                },
                recentActivity[1]);

            Assert.AreEqual(
                new RecentActivity
                {
                    What = RecentActivityKind.NewStory,
                    Who = "someone",
                    Age = TimeSpan.FromMinutes(100),
                    StoryId = 42,
                    StoryTitle = "my story",
                    DetectedAtAge = TimeSpan.FromMinutes(15)
                },
                recentActivity[2]);

            mockStoryQuery.VerifyAll();
            MockCommentDataLayer.VerifyAll();
        }

        [Test]
        public void TestDataLayerSupportsRecentActivityNatively()
        {
            var expected = new[]
            {
                new RecentActivity
                {
                    Age = TimeSpan.FromMinutes(10),
                    CommentId = 100,
                    DetectedAtAge = TimeSpan.FromMinutes(90),
                    StoryId = 42,
                    StoryTitle = "my blog",
                    What = RecentActivityKind.NewComment,
                    Who = "nikolaos"
                }
            };

            var request = new RecentActivityRequest
            {
                MaxCount = 6,
                Username = "nikolaos"
            };

            var recentActivityRepository = Mock.Of<IRecentActivityRepository>(r => r.Get(request) == expected);
            Mock<IDbSession> mockDbSession = Mock.Get(DbSession);
            mockDbSession.Setup(s => s.RecentActivityRepository).Returns(recentActivityRepository);

            var result = ApiService.GetRecentActivity(request);
            CollectionAssert.AreEqual(expected, result);
        }
    }
}
