using System;
using Moq;
using NUnit.Framework;
using BuzzStats.Common;
using BuzzStats.Data;
using BuzzStats.Web.Mvp;

namespace BuzzStats.Tests.Web.Mvp
{
    [TestFixture]
    public class HomePagePresenterTest : PresenterTestBase<IHomePageView>
    {
        [Test]
        public void Test()
        {
            PrepareMocks();

            RecentActivity[] recentActivities = new[]
            {
                new RecentActivity
                {
                    StoryId = 42,
                    StoryTitle = "hello",
                    CommentId = 100
                }
            };

            RecentActivityModel[] recentActivityModels = new[]
            {
                new RecentActivityModel
                {
                    StoryId = 42,
                    StoryTitle = "hello",
                    CommentId = 100,
                    StoryUrl = "http://test.com/42/#100"
                }
            };

            RecentlyCommentedStory[] recentlyCommentedStories = new RecentlyCommentedStory[1];
            CommentSummary[] recentPopularComments = new CommentSummary[1];

            mockApiService.Setup(p => p.GetRecentActivity(null)).Returns(recentActivities);
            mockApiService.Setup(p => p.GetRecentCommentsPerStory()).Returns(recentlyCommentedStories);
            mockApiService.Setup(p => p.GetRecentPopularComments()).Returns(recentPopularComments);

            mockView.SetupSet(v => v.RecentActivities = recentActivityModels);
            mockView.SetupSet(v => v.RecentlyCommentedStories = recentlyCommentedStories);
            mockView.SetupSet(v => v.RecentPopularComments = recentPopularComments);

            HomePagePresenter presenter = new HomePagePresenter(
                mockApiService.Object,
                Mock.Of<IUrlProvider>(p => p.StoryUrl(42, 100) == "http://test.com/42/#100"))
            {
                View = mockView.Object
            };

            mockView.Raise(v => v.ViewLoaded += null, EventArgs.Empty);

            VerifyMocks();
        }
    }
}
