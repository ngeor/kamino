// --------------------------------------------------------------------------------
// <copyright file="QueueManagerTest.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/10/17
// * Time: 07:08:08
// --------------------------------------------------------------------------------

using System;
using System.Collections.Generic;
using System.Configuration;
using Moq;
using NUnit.Framework;
using NGSoftware.Common.Messaging;
using BuzzStats.Boot.Crawler;
using BuzzStats.Common;
using BuzzStats.Crawl;
using BuzzStats.Data;
using BuzzStats.Downloader;
using BuzzStats.Parsing;
using BuzzStats.Persister;
using BuzzStats.Tests.DSL;

namespace BuzzStats.Tests.Crawl
{
    [TestFixture]
    public class QueueManagerTest
    {
        [TestFixtureTearDown]
        public void TestFixtureTearDown()
        {
            Application.ShutDown();
        }

        [Test]
        [Category("Integration")]
        public void ShouldPersistStoriesFromListingSource()
        {
            // arrange
            const string listingUrl = "http://buzz.reality-tape.com/";
            MessageBus messageBus = new MessageBus();
            Story story42 = new Story
            {
                StoryId = 42
            };

            // stories in listing page: 42
            IDownloaderService downloader = new Mock<IDownloaderService>(MockBehavior.Strict).Object
                .SetupDownloadStories(listingUrl, 42)
                .SetupDownloadStory("http://buzz.reality-tape.com/story.php?id=42", 42)
                .Returns(story42);

            Mock<IPersister> mockPersister = new Mock<IPersister>();
            mockPersister.Setup(p => p.Save(story42))
                .Returns(new PersisterResult(new StoryData(), UpdateResult.NewVotes));
            Listing listingSource = new Listing(
                downloader,
                new UrlProvider(),
                listingUrl,
                Mock.Of<IStoryDataLayer>().BindDbContext());
            QueueManager queueManager = new QueueManager(
                messageBus,
                downloader,
                mockPersister.Object,
                listingSource);
            queueManager.AllowedLoops = 1;
            queueManager.InfiniteLoop = false; // might be set to true by CrawlApp

            // act
            queueManager.Start();

            // assert
            Mock.Get(downloader).VerifyAll();
            mockPersister.Verify(p => p.Save(story42), Times.Once());
        }

        [Test]
        [Category("Integration")]
        public void ShouldPersistStoriesFromPoller()
        {
            MessageBus messageBus = new MessageBus();
            Story story42 = new Story();

            // stories in listing page: 42, 43
            IDownloaderService downloader = new Mock<IDownloaderService>(MockBehavior.Strict).Object
                .SetupDownloadStory("http://buzz.reality-tape.com/story.php?id=42", 42)
                .Returns(story42);

            Mock<IPersister> mockPersister = new Mock<IPersister>();
            mockPersister.Setup(p => p.Save(story42))
                .Returns(new PersisterResult(new StoryData(), UpdateResult.NewComments));

            IStoryQuery storyQuery = Mock.Of<IStoryQuery>()
                .SetupTake(10)
                .SetupOrderBy(StorySortField.ModificationAge.Asc())
                .ReturnsEnumerableOfIds(new[] {42});

            IUrlProvider urlProvider = new UrlProvider();
            Poller poller = new Poller(messageBus, storyQuery.BindDbContext(), urlProvider,
                Mock.Of<ILeafProducerMonitor>());
            QueueManager queueManager = new QueueManager(
                messageBus,
                downloader,
                mockPersister.Object,
                poller);
            queueManager.AllowedLoops = 1;
            queueManager.InfiniteLoop = false; // might be set to true by CrawlApp

            // act
            queueManager.Start();

            // assert
            Mock.Get(downloader).VerifyAll();
            mockPersister.Verify(p => p.Save(story42), Times.Once());
        }

        [Test]
        [Category("Integration")]
        public void ShouldResolveDependenciesOfQueueManager()
        {
            var resolver = Application.Boot(new string[0]);
            var queueManager = resolver.GetService(typeof(QueueManager)) as QueueManager;
            Assert.IsNotNull(queueManager);
        }

        [Test]
        [Category("Integration")]
        public void ShouldResolveDependenciesOfIQueueManager()
        {
            var resolver = Application.Boot(new string[0]);
            var queueManager = resolver.GetService(typeof(IQueueManager)) as IQueueManager;
            Assert.IsNotNull(queueManager);
        }

        [Test]
        [Category("Integration")]
        public void ShouldRunOneIteration()
        {
            var resolver = Application.Boot(new string[0]);
            var messageBus = resolver.GetService(typeof(IMessageBus)) as IMessageBus;
            var collectedStoryIds = new HashSet<int>();
            messageBus.Subscribe<StoryDownloadedMessage>(msg =>
            {
                var storyId = msg.Story.StoryId;
                Console.WriteLine("Downloaded story {0}", storyId);
                if (!collectedStoryIds.Add(storyId))
                {
                    throw new InvalidOperationException("Already checked " + storyId);
                }
            });

            var queueManager = resolver.GetService(typeof(QueueManager)) as QueueManager;
            queueManager.AllowedLoops = 1;
            queueManager.InfiniteLoop = false; // might be set to true by CrawlApp
            queueManager.Start();

            int maxTotalChecks = Convert.ToInt32(
                ConfigurationManager
                    .ConnectionStrings["BuzzStats"]
                    .ExecuteScalar("SELECT MAX(TotalChecks) FROM Story"));

            Assert.AreEqual(1, maxTotalChecks);
        }

        private void ShouldRunAsManyIterationsAsTheLimit(int limit)
        {
            const string storyUrl = "http://test.com/42/";
            const int storyId = 42;
            var story = Mock.Of<Story>(s => s.StoryId == storyId);
            var messageBus = new MessageBus();
            var downloaderService = Mock.Of<IDownloaderService>(p => p.DownloadStory(storyUrl, storyId) == story);
            var persister = Mock.Of<IPersister>(
                p => p.Save(story) == new PersisterResult(new StoryData(), UpdateResult.NewVotes));
            var source = new StoryLeaf(storyUrl, storyId, Mock.Of<ILeafSource>());

            QueueManager queueManager = new QueueManager(
                messageBus,
                downloaderService,
                persister,
                source);
            queueManager.AllowedLoops = limit;
            queueManager.Start();
            Mock.Get(persister).Verify(p => p.Save(story), Times.Exactly(limit));
        }

        [Test]
        [Timeout(500)]
        public void ShouldRunAsManyIterationsAsTheLimit0()
        {
            ShouldRunAsManyIterationsAsTheLimit(0);
        }

        [Test]
        [Timeout(500)]
        public void ShouldRunAsManyIterationsAsTheLimit1()
        {
            ShouldRunAsManyIterationsAsTheLimit(1);
        }

        [Test]
        [Timeout(500)]
        public void ShouldRunAsManyIterationsAsTheLimit2()
        {
            ShouldRunAsManyIterationsAsTheLimit(2);
        }

        [Test]
        [Timeout(500)]
        public void ShouldRunAsManyIterationsAsTheLimit3()
        {
            ShouldRunAsManyIterationsAsTheLimit(3);
        }

        [Test]
        [Timeout(500)]
        public void ShouldRunInfinitelyWhenTheFlagIsSet()
        {
            const string storyUrl = "http://test.com/42/";
            const int storyId = 42;
            const int expectedIterations = 1024;
            var story = Mock.Of<Story>(s => s.StoryId == storyId);
            var messageBus = new MessageBus();
            var downloaderService = Mock.Of<IDownloaderService>(p => p.DownloadStory(storyUrl, storyId) == story);
            var persister = Mock.Of<IPersister>(
                p => p.Save(story) == new PersisterResult(new StoryData(), UpdateResult.Created));
            var source = new StoryLeaf(storyUrl, storyId, Mock.Of<ILeafSource>());
            var loopCount = 0;

            QueueManager queueManager = new QueueManager(
                messageBus,
                downloaderService,
                persister,
                source);
            queueManager.InfiniteLoop = true;

            // hook-in to be able to shut down queueManager
            messageBus.Subscribe<LeafConsumerStartingMessage>(msg =>
            {
                loopCount++;
                if (loopCount >= expectedIterations)
                {
                    queueManager.InfiniteLoop = false;
                }
            });

            queueManager.Start();
            Mock.Get(persister).Verify(p => p.Save(story), Times.Exactly(expectedIterations));
        }
    }
}