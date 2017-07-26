// --------------------------------------------------------------------------------
// <copyright file="AboutPagePresenterTest.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2014
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2014/08/09
// * Time: 19:15:11
// --------------------------------------------------------------------------------

using System;
using BuzzStats.Data;
using BuzzStats.Services;
using BuzzStats.Web.Mvp;
using Moq;
using NGSoftware.Common;
using NUnit.Framework;

namespace BuzzStats.UnitTests.Web.Mvp
{
    [TestFixture]
    public class AboutPagePresenterTest : PresenterTestBase<IAboutPageView>
    {
        private Mock<IStoryDataLayer> _mockStoryDataLayer = new Mock<IStoryDataLayer>();

        [Test]
        public void CanLoadAboutPagePresenter()
        {
            PrepareMocks();
            SetupMinMaxStats();

            var oldestCheckedStory = new StorySummary();

            mockApiService
                .Setup(p => p.GetStorySummaries(new GetStorySummariesRequest
                {
                    MaxRows = 1,
                    SortBy = new[] {StorySortField.LastCheckedAt.Asc()}
                }))
                .Returns(new[]
                {
                    oldestCheckedStory
                });

            mockView.Setup(p => p.SetOldestCheckedStory(oldestCheckedStory));
            mockView.Setup(p => p.SetEchoSucceeded(false));
            mockView.Setup(p => p.SetUpTime(TimeSpan.Zero));

            AboutPagePresenter presenter = new AboutPagePresenter(
                mockApiService.Object,
                Mock.Of<IDbSession>(x => x.Stories == _mockStoryDataLayer.Object),
                Mock.Of<IDiagnosticsService>())
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

        private void SetupMinMaxStats()
        {
            MinMaxValue<int> totalChecks = new MinMaxValue<int>
            {
                Min = 10,
                Max = 20
            };
            MinMaxValue<DateTime> lastCheckedAt = new MinMaxValue<DateTime>
            {
                Min = new DateTime(2010, 1, 1),
                Max = new DateTime(2014, 4, 4)
            };

            _mockStoryDataLayer.Setup(p => p.GetMinMaxStats()).Returns(
                new MinMaxStats
                {
                    LastCheckedAt = lastCheckedAt,
                    TotalChecks = totalChecks
                });

            mockView.Setup(p => p.SetMinMaxStats(It.Is<MinMaxStats>(
                m => m.LastCheckedAt.Equals(lastCheckedAt) && m.TotalChecks.Equals(totalChecks))));
        }
    }
}