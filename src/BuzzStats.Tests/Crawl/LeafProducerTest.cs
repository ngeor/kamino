// --------------------------------------------------------------------------------
// <copyright file="LeafProducerTest.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/10/17
// * Time: 17:44:19
// --------------------------------------------------------------------------------

using System.Linq;
using Moq;
using NUnit.Framework;
using NGSoftware.Common.Messaging;
using BuzzStats.Common;
using BuzzStats.Crawl;
using BuzzStats.Data;
using BuzzStats.Downloader;
using BuzzStats.Parsing;
using BuzzStats.Tests.DSL;
using BuzzStats.Tests.Utils;

namespace BuzzStats.Tests.Crawl
{
    [TestFixture]
    public class LeafProducerTest
    {
        [Test]
        public void ShouldNotifyItIsStartingWhenStartIsCalled()
        {
            // arrange
            var messageBus = Mock.Of<IMessageBus>();
            ILeaf leaf = Mock.Of<ILeaf>();
            LeafProducer leafProducer = new LeafProducer(messageBus, leaf);

            // act
            leafProducer.Start();

            // assert
            messageBus.VerifySingleMessage<LeafProducerStartingMessage>();
        }

        [Test]
        public void ShouldNotNotifyItIsStartingWhenStartIsNotCalled()
        {
            // arrange
            IMessageBus messageBus = Mock.Of<IMessageBus>();
            ILeaf leaf = Mock.Of<ILeaf>();

            // act
            LeafProducer leafProducer = new LeafProducer(messageBus, leaf);

            // assert
            messageBus.VerifyNoMessage<LeafProducerStartingMessage>();
        }

        [Test]
        public void ShouldNotifyWhenFinished()
        {
            // arrange
            IMessageBus messageBus = Mock.Of<IMessageBus>();
            ILeaf leaf = Mock.Of<ILeaf>();
            LeafProducer leafProducer = new LeafProducer(messageBus, leaf);

            // act
            leafProducer.Start();

            // assert
            messageBus.VerifySingleMessage<LeafProducerFinishedMessage>();
        }

        [Test]
        public void ShouldFirstNotifyItStartedAndThenThatItFinished()
        {
            // arrange
            bool finishedMessageSent = false;
            Mock<IMessageBus> mockMessageBus = new Mock<IMessageBus>();
            mockMessageBus
                .Setup(p => p.Publish(It.IsAny<LeafProducerStartingMessage>()))
                .Callback(() => Assert.IsFalse(finishedMessageSent, "finished message sent too early"));
            mockMessageBus.Setup(p => p.Publish(It.IsAny<LeafProducerFinishedMessage>()))
                .Callback(() => finishedMessageSent = true);
            ILeaf leaf = Mock.Of<ILeaf>();
            LeafProducer leafProducer = new LeafProducer(mockMessageBus.Object, leaf);

            // act
            leafProducer.Start();

            // assert
            Assert.IsTrue(finishedMessageSent);
        }

        [Test]
        public void ShouldNotContainLeafBeforeStart()
        {
            // arrange
            ILeaf leaf = Mock.Of<ILeaf>();
            IMessageBus messageBus = Mock.Of<IMessageBus>();
            LeafProducer leafProducer = new LeafProducer(
                messageBus,
                leaf);

            // assert
            messageBus.VerifyNoMessage<LeafProducerFinishedMessage>();
        }

        [Test]
        public void ShouldSendMessagesAboutFoundLeaves()
        {
            // arrange
            ILeaf leaf = Mock.Of<ILeaf>();
            IMessageBus messageBus = Mock.Of<IMessageBus>();
            LeafProducer leafProducer = new LeafProducer(
                messageBus,
                leaf);

            // act
            leafProducer.Start();

            // assert
            messageBus.VerifySingleMessage<LeafProducerFoundLeafMessage>(m => m.Leaf.Equals(leaf));
        }

        [Test]
        public void ShouldSupportLeafAsASeedSource()
        {
            // arrange
            ILeaf leaf = Mock.Of<ILeaf>();
            IMessageBus messageBus = Mock.Of<IMessageBus>();
            LeafProducer leafProducer = new LeafProducer(
                messageBus,
                leaf);

            // act
            leafProducer.Start();

            // assert
            messageBus.VerifySingleMessage<LeafProducerFinishedMessage>(m => m.Leaves.Contains(leaf));
        }

        [Test]
        public void ShouldSupportOneLevelSource()
        {
            // arrange
            ILeaf leaf = Mock.Of<ILeaf>();
            ISource seedSource = Mock.Of<ISource>(p => p.GetChildren() == new[] {leaf});
            IMessageBus messageBus = Mock.Of<IMessageBus>();
            LeafProducer leafProducer = new LeafProducer(
                messageBus,
                seedSource);

            // act
            leafProducer.Start();

            // assert
            messageBus.VerifySingleMessage<LeafProducerFinishedMessage>(m => m.Leaves.Contains(leaf));
        }

        [Test]
        public void ShouldSupportTwoLevelSource()
        {
            // arrange
            ILeaf leaf = Mock.Of<ILeaf>();
            ISource middleSource = Mock.Of<ISource>(p => p.GetChildren() == new[] {leaf});
            ISource seedSource = Mock.Of<ISource>(p => p.GetChildren() == new[] {middleSource});
            IMessageBus messageBus = Mock.Of<IMessageBus>();
            LeafProducer leafProducer = new LeafProducer(
                messageBus,
                seedSource);

            // act
            leafProducer.Start();

            // assert
            messageBus.VerifySingleMessage<LeafProducerFinishedMessage>(m => m.Leaves.Contains(leaf));
        }

        [Test]
        public void ShouldSupportUnevenDepthSource()
        {
            // arrange
            ILeaf leaf1 = Mock.Of<ILeaf>();
            ILeaf leaf2 = Mock.Of<ILeaf>();
            ISource middleSource = Mock.Of<ISource>(p => p.GetChildren() == new[] {leaf2});
            ISource seedSource = Mock.Of<ISource>(p => p.GetChildren() == new[] {leaf1, middleSource});
            IMessageBus messageBus = Mock.Of<IMessageBus>();
            LeafProducer leafProducer = new LeafProducer(
                messageBus,
                seedSource);

            // act
            leafProducer.Start();

            // assert
            messageBus.VerifySingleMessage<LeafProducerFinishedMessage>(m => m.Leaves.Contains(leaf1));
            messageBus.VerifySingleMessage<LeafProducerFinishedMessage>(m => m.Leaves.Contains(leaf2));
        }

        [Test]
        public void ShouldNotCollectTheSameLeafTwice()
        {
            // arrange
            ILeaf leaf = Mock.Of<ILeaf>();
            ISource middleSource = Mock.Of<ISource>(p => p.GetChildren() == new[] {leaf});
            ISource seedSource = Mock.Of<ISource>(p => p.GetChildren() == new[] {leaf, middleSource});
            IMessageBus messageBus = Mock.Of<IMessageBus>();
            LeafProducer leafProducer = new LeafProducer(
                messageBus,
                seedSource);

            // act
            leafProducer.Start();

            // assert
            messageBus.VerifySingleMessage<LeafProducerFinishedMessage>(m => m.Leaves.Count() == 1);
        }

        [Test]
        public void ShouldNotStartAgainWhenConsumerFinishes()
        {
            // arrange
            var messageBus = Mock.Of<IMessageBus>();
            messageBus.SendDefaultReplyUponSubscription<LeafConsumerFinishedMessage>();
            var source = Mock.Of<ILeaf>();

            // act
            LeafProducer leafProducer = new LeafProducer(
                messageBus,
                source);

            // assert
            messageBus.VerifyNoMessage<LeafProducerStartingMessage>();
        }

        [Test]
        public void ShouldAllowPollerToExcludeStoryIdsRetrievedFromListings()
        {
            IStoryQuery storyQuery = new Mock<IStoryQuery>(MockBehavior.Strict).Object
                .SetupExcludeIds(new[] {123})
                .SetupTake(10)
                .SetupOrderBy(StorySortField.ModificationAge.Asc())
                .ReturnsEnumerableOfIds(new[] {42});

            var messageBus = new StubMessageBus();
            var downloader = Mock.Of<IDownloaderService>()
                .SetupDownloadStories("http://test.com/", 123)
                .SetupDownloadStory("http://test.com/42/", 42)
                .Returns(new Story())
                .SetupDownloadStory("http://test.com/123/", 123)
                .Returns(new Story());

            var storyUrlProvider = Mock.Of<IUrlProvider>()
                .SetupStoryUrl(42, "http://test.com/42/")
                .SetupStoryUrl(123, "http://test.com/123/");

            var dbContext = storyQuery.BindDbContext();
            var leafProducerMonitor = new LeafProducerMonitor(messageBus);
            var source = new AggregateSource(
                new Listing(downloader, storyUrlProvider, "http://test.com/", dbContext),
                new Poller(messageBus, dbContext, storyUrlProvider, leafProducerMonitor)
            );
            LeafProducer leafProducer = new LeafProducer(messageBus, source);
            leafProducer.Start();

            Mock.Get(storyQuery).VerifyAll();
        }
    }
}