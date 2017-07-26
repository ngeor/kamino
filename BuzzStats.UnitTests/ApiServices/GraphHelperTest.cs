using System;
using BuzzStats.Data;
using Moq;
using NGSoftware.Common;
using NUnit.Framework;

namespace BuzzStats.UnitTests.ApiServices
{
    partial class ApiServiceTest
    {
        private Mock<IStoryQuery> SetupStoryCount(DateRange dateRange, int result)
        {
            var mockStoryQuery = new Mock<IStoryQuery>(MockBehavior.Strict);
            var mockCreatedAtFilter = new Mock<IStoryQueryDateFilter>(MockBehavior.Strict);
            mockStoryQuery.Setup(p => p.CreatedAt).Returns(mockCreatedAtFilter.Object);
            mockCreatedAtFilter.Setup(p => p.InRange(dateRange)).Returns(mockStoryQuery.Object);
            mockStoryQuery.Setup(p => p.Count()).Returns(result);
            return mockStoryQuery;
        }

        [Test]
        public void TestGetStoryCount()
        {
            MockStoryDataLayer.Setup(p => p.OldestStoryDate()).Returns(new DateTime(2009, 2, 1));

            Mock<IStoryQuery>[] expectedQueryMocks = new[]
            {
                SetupStoryCount(DateRange.Create(new DateTime(2010, 1, 1), new DateTime(2010, 2, 1)), 1),
                SetupStoryCount(DateRange.Create(new DateTime(2010, 2, 1), new DateTime(2010, 3, 1)), 2),
                SetupStoryCount(DateRange.Create(new DateTime(2010, 3, 1), new DateTime(2010, 4, 1)), 3),
                SetupStoryCount(DateRange.Create(new DateTime(2010, 4, 1), new DateTime(2010, 5, 1)), 5),
                SetupStoryCount(DateRange.Create(new DateTime(2010, 5, 1), new DateTime(2010, 6, 1)), 8),
                SetupStoryCount(DateRange.Create(new DateTime(2010, 6, 1), new DateTime(2010, 7, 1)), 13),
                SetupStoryCount(DateRange.Create(new DateTime(2010, 7, 1), new DateTime(2010, 8, 1)), 21),
                SetupStoryCount(DateRange.Create(new DateTime(2010, 8, 1), new DateTime(2010, 9, 1)), 34),
                SetupStoryCount(DateRange.Create(new DateTime(2010, 9, 1), new DateTime(2010, 10, 1)), 55),
                SetupStoryCount(DateRange.Create(new DateTime(2010, 10, 1), new DateTime(2010, 11, 1)), 89),
                SetupStoryCount(DateRange.Create(new DateTime(2010, 11, 1), new DateTime(2010, 12, 1)), 144),
                SetupStoryCount(DateRange.Create(new DateTime(2010, 12, 1), new DateTime(2011, 1, 1)), 233)
            };

            int idxMock = 0;
            foreach (var m in expectedQueryMocks)
            {
                MockStoryDataLayer.Setup(p => p.Query()).Returns(() =>
                {
                    var nextMock = expectedQueryMocks[idxMock];
                    idxMock++;
                    return nextMock.Object;
                });
            }

            var result = ApiService.GetStoryCountStats(new CountStatsRequest
            {
                Start = new DateTime(2010, 1, 1),
                Stop = new DateTime(2011, 1, 1),
                Interval = DateTimeUnit.Month
            }).Data;
            Assert.IsNotNull(result);
            Assert.AreEqual(12, result.Length);
            for (int i = 0; i < 12; i++)
            {
                //Assert.AreEqual(new DateTime(2010, i + 1, 1), result[i].X);
                Assert.AreEqual(fib(i + 1), result[i]);
            }

            foreach (var m in expectedQueryMocks)
            {
                m.VerifyAll();
            }
        }

        private int fib(int n)
        {
            return n <= 2 ? n : fib(n - 1) + fib(n - 2);
        }

        [Test]
        public void TestGetCommentCount()
        {
            MockStoryDataLayer.Setup(p => p.OldestStoryDate()).Returns(new DateTime(2009, 2, 1));
            MockCommentDataLayer
                .Setup(p => p.Count(DateRange.Create(new DateTime(2010, 1, 1), new DateTime(2010, 1, 8))))
                .Returns(1);
            MockCommentDataLayer
                .Setup(p => p.Count(DateRange.Create(new DateTime(2010, 1, 8), new DateTime(2010, 1, 15))))
                .Returns(2);
            MockCommentDataLayer
                .Setup(p => p.Count(DateRange.Create(new DateTime(2010, 1, 15), new DateTime(2010, 1, 22))))
                .Returns(3);
            MockCommentDataLayer
                .Setup(p => p.Count(DateRange.Create(new DateTime(2010, 1, 22), new DateTime(2010, 1, 29))))
                .Returns(5);
            MockCommentDataLayer
                .Setup(p => p.Count(DateRange.Create(new DateTime(2010, 1, 29), new DateTime(2010, 2, 1))))
                .Returns(8);

            var result = ApiService.GetCommentCountStats(
                new CountStatsRequest
                {
                    Start = new DateTime(2010, 1, 1),
                    Stop = new DateTime(2010, 2, 1),
                    Interval = DateTimeUnit.Week
                }).Data;

            Assert.IsNotNull(result);
            Assert.AreEqual(5, result.Length);
            for (int i = 0; i < 5; i++)
            {
                //Assert.AreEqual(new DateTime(2010, 1, 1 + i * 7), result[i].X);
                Assert.AreEqual(fib(i + 1), result[i]);
            }
        }
    }
}