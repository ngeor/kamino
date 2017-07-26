// --------------------------------------------------------------------------------
// <copyright file="ConfiguredPollersTest.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/10/22
// * Time: 05:47:51
// --------------------------------------------------------------------------------

using System.Linq;
using BuzzStats.Common;
using BuzzStats.Crawl;
using BuzzStats.Data;
using Moq;
using NGSoftware.Common.Messaging;
using NUnit.Framework;

namespace BuzzStats.UnitTests.Crawl
{
    [TestFixture]
    public class ConfiguredPollersTest
    {
        [Test]
        public void ShouldReturnThePoller()
        {
            var messageBus = Mock.Of<IMessageBus>();
            var dbContext = Mock.Of<IDbContext>();
            var storyUrlProvider = Mock.Of<IUrlProvider>();
            var configuration = Mock.Of<IConfiguration>();
            var leafProducerMonitor = Mock.Of<ILeafProducerMonitor>();
            var configuredPollers = new ConfiguredPollers(
                messageBus,
                dbContext,
                storyUrlProvider,
                configuration,
                leafProducerMonitor);
            var children = configuredPollers.GetChildren().ToArray();
            CollectionAssert.AreEqual(
                new[] {new Poller(messageBus, dbContext, storyUrlProvider, leafProducerMonitor)},
                children);
        }

        [Test]
        public void ShouldReturnTheSamePollerInstance()
        {
            var messageBus = Mock.Of<IMessageBus>();
            var dbContext = Mock.Of<IDbContext>();
            var storyUrlProvider = Mock.Of<IUrlProvider>();
            var configuration = Mock.Of<IConfiguration>();
            var leafProducerMonitor = Mock.Of<ILeafProducerMonitor>();
            var configuredPollers = new ConfiguredPollers(
                messageBus,
                dbContext,
                storyUrlProvider,
                configuration,
                leafProducerMonitor);
            var poller1 = configuredPollers.GetChildren().First();
            var poller2 = configuredPollers.GetChildren().First();
            Assert.AreSame(poller1, poller2);
        }

        [Test]
        public void ShouldProvideNothingWhenSkipPollersIsOn()
        {
            var dbContext = Mock.Of<IDbContext>();
            var storyUrlProvider = Mock.Of<IUrlProvider>();
            var configuration = Mock.Of<IConfiguration>(
                c => c.SkipPollers == true);
            var configuredPollers = new ConfiguredPollers(
                Mock.Of<IMessageBus>(),
                dbContext,
                storyUrlProvider,
                configuration,
                Mock.Of<ILeafProducerMonitor>());
            var children = configuredPollers.GetChildren().ToArray();
            Assert.AreEqual(0, children.Length);
        }
    }
}