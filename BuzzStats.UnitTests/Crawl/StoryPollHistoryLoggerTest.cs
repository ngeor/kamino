// --------------------------------------------------------------------------------
// <copyright file="StoryPollHistoryLoggerTest.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/11/22
// * Time: 06:50:44
// --------------------------------------------------------------------------------

using System;
using BuzzStats.Crawl;
using BuzzStats.Data;
using BuzzStats.Persister;
using BuzzStats.UnitTests.DSL;
using BuzzStats.UnitTests.Utils;
using Moq;
using NGSoftware.Common;
using NUnit.Framework;

namespace BuzzStats.UnitTests.Crawl
{
    [TestFixture]
    public class StoryPollHistoryLoggerTest
    {
        [Test]
        public void ShouldSavePollHistoryWhenAStoryIsChecked()
        {
            TestableDateTime.UtcNowStrategy = () => new DateTime(2015, 11, 22);
            StoryData story = new StoryData(42);
            StubMessageBus messageBus = new StubMessageBus();
            StoryPollHistoryData data = new StoryPollHistoryData
            {
                Story = story,
                SourceId = "poller",
                HadChanges = (int) UpdateResult.Created,
                CheckedAt = new DateTime(2015, 11, 22)
            };

            var mockStoryPollHistoryDataLayer = new Mock<IStoryPollHistoryDataLayer>(MockBehavior.Strict);
            mockStoryPollHistoryDataLayer.Setup(p => p.Create(data)).Returns(data);
            StoryPollHistoryLogger logger = new StoryPollHistoryLogger(messageBus,
                mockStoryPollHistoryDataLayer.Object.BindDbContext());
            messageBus.Publish(new StoryCheckedMessage(
                story,
                Mock.Of<ILeafSource>(s => s.SourceId == "poller"),
                UpdateResult.Created));
            mockStoryPollHistoryDataLayer.VerifyAll();
        }

        [TearDown]
        public void TearDown()
        {
            TestableDateTime.UtcNowStrategy = null;
        }
    }
}