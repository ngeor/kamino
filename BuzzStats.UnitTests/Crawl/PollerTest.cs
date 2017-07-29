// --------------------------------------------------------------------------------
// <copyright file="PollerTest.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/10/17
// * Time: 04:44:49
// --------------------------------------------------------------------------------

using System.Collections.Generic;
using System.Linq;
using BuzzStats.Common;
using BuzzStats.Crawl;
using BuzzStats.Data;
using BuzzStats.Persister;
using BuzzStats.UnitTests.DSL;
using BuzzStats.UnitTests.Utils;
using Moq;
using NGSoftware.Common.Messaging;
using NUnit.Framework;

namespace BuzzStats.UnitTests.Crawl
{
    [TestFixture]
    public class PollerTest
    {
        [Test]
        public void ShouldSelect10MostRecentlyModifiedStories()
        {
            // arrange
            IStoryQuery storyQuery = new Mock<IStoryQuery>(MockBehavior.Strict).Object
                .SetupTake(10)
                .SetupOrderBy(StorySortField.ModificationAge.Asc())
                .ReturnsEnumerableOfIds(new[] {42});

            IUrlProvider urlProvider = Mock.Of<IUrlProvider>()
                .SetupStoryUrl(42, "http://test.com/");

            Poller poller = Create(dbContext: storyQuery.BindDbContext(), urlProvider: urlProvider);

            // act
            StoryLeaf[] result = poller.GetChildren().Cast<StoryLeaf>().ToArray();

            // assert
            Assert.That(result, Is.EqualTo(new[] {new StoryLeaf("http://test.com/", 42, poller)}));
        }

        [Test]
        public void ShouldSelect20MostRecentlyModifiedStories()
        {
            // arrange
            IStoryQuery storyQuery = new Mock<IStoryQuery>(MockBehavior.Strict).Object
                .SetupTake(20)
                .SetupOrderBy(StorySortField.ModificationAge.Asc())
                .ReturnsEnumerableOfIds(new[] {42});

            IUrlProvider urlProvider = Mock.Of<IUrlProvider>()
                .SetupStoryUrl(42, "http://test.com/");

            Poller poller = Create(dbContext: storyQuery.BindDbContext(), urlProvider: urlProvider, count: 20);

            // act
            StoryLeaf[] result = poller.GetChildren().Cast<StoryLeaf>().ToArray();

            // assert
            CollectionAssert.AreEqual(new[] {new StoryLeaf("http://test.com/", 42, poller)}, result);
        }

        [Test]
        public void ShouldBeEqualToOtherPoller()
        {
            int count = 20;
            Poller a = Create(count: count);
            Poller b = Create(count: count);
            Assert.AreEqual(a, b);
        }

        [Test]
        public void ShouldNotBeEqualWhenCountIsDifferent()
        {
            Poller a = Create(count: 15);
            Poller b = Create(count: 20);
            Assert.AreNotEqual(a, b);
        }

        [Test]
        public void ShouldUseCountInSourceId()
        {
            Poller poller = Create(count: 20);
            Assert.AreEqual("poller20", poller.SourceId);
        }

        [Test]
        public void ShouldStopIgnoringStoriesAfter10ProducerCycles()
        {
            // arrange
            StubMessageBus messageBus = new StubMessageBus();
            IStoryQuery storyQuery = StubStoryQuery.Mock()
                .SetupExcludeIds(new[] {123});

            Poller poller = Create(
                messageBus,
                storyQuery.BindDbContext());
            poller.CycleCount = 10;

            messageBus.Publish(new StoryCheckedMessage(new StoryData(123), poller, UpdateResult.NoChanges));
            foreach (var x in Enumerable.Range(1, 10))
            {
                messageBus.Publish(new LeafProducerStartingMessage());
            }

            // act
            poller.GetChildren();

            // assert
            Mock.Get(storyQuery).Verify(v => v.ExcludeIds(It.IsAny<IEnumerable<int>>()), Times.Never());
        }

        private static Poller Create(
            IMessageBus messageBus = null,
            IDbContext dbContext = null,
            IUrlProvider urlProvider = null,
            ILeafProducerMonitor leafProducerMonitor = null,
            int? count = null)
        {
            var poller = new Poller(
                messageBus ?? Mock.Of<IMessageBus>(),
                dbContext ?? Mock.Of<IDbContext>(),
                urlProvider ?? Mock.Of<IUrlProvider>(),
                leafProducerMonitor ?? Mock.Of<ILeafProducerMonitor>());

            if (count.HasValue)
            {
                poller.Count = count.Value;
            }

            return poller;
        }
    }
}