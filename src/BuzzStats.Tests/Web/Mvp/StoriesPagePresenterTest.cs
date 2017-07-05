// --------------------------------------------------------------------------------
// <copyright file="StoriesPagePresenterTest.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2014
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2014/08/09
// * Time: 19:15:11
// --------------------------------------------------------------------------------

using System;
using Moq;
using NUnit.Framework;
using NGSoftware.Common;
using BuzzStats.Data;
using BuzzStats.Web.Mvp;

namespace BuzzStats.Tests.Web.Mvp
{
    [TestFixture]
    public class StoriesPagePresenterTest : PresenterTestBase<IStoriesPageView>
    {
        private Mock<IStoryDataLayer> _mockStoryDataLayer = new Mock<IStoryDataLayer>();

        [Test]
        public void CanLoadStoriesPagePresenter()
        {
            PrepareMocks();
            SetupCommentCount();
            SetupStoryLists();

            StoriesPagePresenter presenter = new StoriesPagePresenter(mockApiService.Object)
            {
                View = mockView.Object
            };

            mockView.Raise(v => v.ViewLoaded += null, EventArgs.Empty);

            VerifyMocks();
        }

        protected override void PrepareMocks()
        {
            base.PrepareMocks();
            _mockStoryDataLayer = new Mock<IStoryDataLayer>(MockBehavior.Strict);
        }

        protected override void VerifyMocks()
        {
            base.VerifyMocks();
            _mockStoryDataLayer.VerifyAll();
        }

        private void SetupStoryLists()
        {
            var lastModifiedStories = new StorySummary[0];
            var recentStories = new StorySummary[0];
            var lastCommentedStories = new StorySummary[0];

            mockApiService
                .Setup(
                    p => p.GetStorySummaries(new GetStorySummariesRequest(StorySortField.LastModifiedAt.Desc(), 0, 10)))
                .Returns(lastModifiedStories);
            mockApiService
                .Setup(p => p.GetStorySummaries(new GetStorySummariesRequest(StorySortField.CreatedAt.Desc(), 0, 10)))
                .Returns(recentStories);
            mockApiService
                .Setup(
                    p => p.GetStorySummaries(new GetStorySummariesRequest(StorySortField.LastCommentedAt.Desc(), 0,
                        10)))
                .Returns(lastCommentedStories);

            mockView.Setup(p => p.SetStories(StorySortField.LastModifiedAt, lastModifiedStories));
            mockView.Setup(p => p.SetStories(StorySortField.CreatedAt, recentStories));
            mockView.Setup(p => p.SetStories(StorySortField.LastCommentedAt, lastCommentedStories));
        }

        private void SetupCommentCount()
        {
            mockApiService
                .Setup(p => p.GetCommentCountStats(new CountStatsRequest
                {
                    Interval = DateTimeUnit.Year
                }))
                .Returns(new CountStatsResponse
                {
                    Start = new DateTime(2010, 12, 5),
                    Data = new[] {60, 40}
                });

            mockView.Setup(p => p.SetCommentCount(100));
        }
    }
}