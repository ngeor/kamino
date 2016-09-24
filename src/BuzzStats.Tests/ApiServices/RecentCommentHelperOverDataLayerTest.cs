// --------------------------------------------------------------------------------
// <copyright file="RecentCommentHelperOverDataLayerTest.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/07/04
// * Time: 9:37 πμ
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
        /// Tests the <see cref="RecentCommentHelperOverDataLayer.GetRecentlyCommented"/> method.
        /// </summary>
        [Test]
        public void TestGetRecentlyCommented()
        {
            Mock<IStoryQuery> mockStoryQuery = new Mock<IStoryQuery>(MockBehavior.Strict);
            MockStoryDataLayer.Setup(p => p.Query()).Returns(mockStoryQuery.Object);
            mockStoryQuery.Setup(p => p.Take(6)).Returns(mockStoryQuery.Object);
            mockStoryQuery.Setup(p => p.OrderBy(StorySortField.LastCommentedAt.Desc()))
                .Returns(mockStoryQuery.Object);

            mockStoryQuery.Setup(p => p.AsEnumerable()).Returns(new[]
            {
                new StoryData {StoryId = 100, Title = "my story"},
                new StoryData {StoryId = 200, Title = "my story 2"},
                new StoryData {StoryId = 300, Title = "my story 3"},
            });

            MockCommentDataLayer.Setup(p => p.Query(new CommentDataQueryParameters
            {
                Count = 5,
                StoryId = 100,
                SortBy = new[] {CommentSortField.CreatedAt.Desc()}
            }))
                .Returns(new[]
                {
                    new CommentData
                    {
                        CommentId = 356,
                        VotesUp = 5,
                        Username = "nikolaos",
                        CreatedAt = TestableDateTime.UtcNow.Subtract(TimeSpan.FromMinutes(100))
                    }
                });

            MockCommentDataLayer.Setup(p => p.Query(new CommentDataQueryParameters
            {
                Count = 5,
                StoryId = 200,
                SortBy = new[] {CommentSortField.CreatedAt.Desc()}
            }))
                .Returns(new[]
                {
                    new CommentData {CommentId = 444}
                });

            MockCommentDataLayer.Setup(p => p.Query(new CommentDataQueryParameters
            {
                Count = 5,
                StoryId = 300,
                SortBy = new[] {CommentSortField.CreatedAt.Desc()}
            }))
                .Returns(new[]
                {
                    new CommentData {CommentId = 555, CreatedAt = TestableDateTime.UtcNow},
                    new CommentData
                    {
                        CommentId = 666,
                        CreatedAt = TestableDateTime.UtcNow.Subtract(TimeSpan.FromHours(1))
                    }
                });

            var recentStories = ApiService.GetRecentCommentsPerStory();
            Assert.IsNotNull(recentStories);
            Assert.AreEqual(3, recentStories.Length);
            Assert.AreEqual(
                new RecentlyCommentedStory
                {
                    StoryId = 100,
                    Title = "my story",
                    Comments = new[]
                    {
                        new RecentlyCommentedStory.Comment
                        {
                            Age = TimeSpan.FromMinutes(100),
                            CommentId = 356,
                            VotesUp = 5,
                            Username = "nikolaos"
                        }
                    }
                },
                recentStories[0]);

            Assert.AreEqual(
                new RecentlyCommentedStory
                {
                    StoryId = 300,
                    Title = "my story 3",
                    Comments = new[]
                    {
                        new RecentlyCommentedStory.Comment {CommentId = 555, Age = TimeSpan.Zero},
                        new RecentlyCommentedStory.Comment {CommentId = 666, Age = TimeSpan.FromHours(1)}
                    }
                },
                recentStories[2]);

            mockStoryQuery.VerifyAll();
            MockCommentDataLayer.VerifyAll();
        }
    }
}
