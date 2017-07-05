// --------------------------------------------------------------------------------
// <copyright file="LeafConsumerTest.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/10/17
// * Time: 18:27:01
// --------------------------------------------------------------------------------

using Moq;
using NUnit.Framework;
using NGSoftware.Common.Messaging;
using BuzzStats.Crawl;
using BuzzStats.Downloader;
using BuzzStats.Tests.DSL;

namespace BuzzStats.Tests.Crawl
{
    [TestFixture]
    public class LeafConsumerTest
    {
        [Test]
        public void ShouldStartWhenProducerIsFinished()
        {
            // arrange
            var producerMessage = new LeafProducerFinishedMessage(new ILeaf[0]);
            IMessageBus messageBus = Mock.Of<IMessageBus>()
                .SendUponSubscription(producerMessage);

            // act
            LeafConsumer leafConsumer = new LeafConsumer(messageBus, Mock.Of<IDownloaderService>());

            // assert
            messageBus.VerifySingleMessage<LeafConsumerStartingMessage>();
        }

        [Test]
        public void ShouldNotifyWhenFinished()
        {
            // arrange
            var producerMessage = new LeafProducerFinishedMessage(new ILeaf[0]);
            IMessageBus messageBus = Mock.Of<IMessageBus>()
                .SendUponSubscription(producerMessage);

            // act
            LeafConsumer leafConsumer = new LeafConsumer(messageBus, Mock.Of<IDownloaderService>());

            // assert
            messageBus.VerifySingleMessage<LeafConsumerFinishedMessage>();
        }

        [Test]
        public void ShouldFirstNotifyItStartedAndThenThatItFinished()
        {
            // arrange
            bool finishedMessageSent = false;
            var producerMessage = new LeafProducerFinishedMessage(new ILeaf[0]);
            IMessageBus messageBus = Mock.Of<IMessageBus>()
                .SendUponSubscription(producerMessage)
                .OnReceive<LeafConsumerStartingMessage>(
                    msg => Assert.IsFalse(finishedMessageSent, "finished message sent too early"))
                .OnReceive<LeafConsumerFinishedMessage>(
                    msg => finishedMessageSent = true);

            // act
            LeafConsumer leafConsumer = new LeafConsumer(messageBus, Mock.Of<IDownloaderService>());

            // assert
            Assert.IsTrue(finishedMessageSent);
        }

        [Test]
        public void ShouldProcessLeaves()
        {
            // arrange
            IDownloaderService downloader = Mock.Of<IDownloaderService>();
            ILeaf leaf = Mock.Of<ILeaf>();
            var producerMessage = new LeafProducerFinishedMessage(new[] {leaf});
            IMessageBus messageBus = Mock.Of<IMessageBus>()
                .SendUponSubscription(producerMessage);

            // act
            LeafConsumer leafConsumer = new LeafConsumer(messageBus, downloader);

            // assert
            Mock.Get(leaf).Verify(p => p.Update(downloader, messageBus), Times.Once());
        }
    }
}