// --------------------------------------------------------------------------------
// <copyright file="ApplicationTest.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/09/30
// * Time: 8:26 πμ
// --------------------------------------------------------------------------------

using System;
using NUnit.Framework;
using StructureMap;
using NGSoftware.Common.Messaging;
using BuzzStats.Boot;
using BuzzStats.Boot.Crawler;
using BuzzStats.Crawl;
using BuzzStats.Services;

namespace BuzzStats.Tests.Boot.Crawler
{
    [TestFixture]
    [Category("Integration")]
    public class ApplicationTest
    {
        private IServiceProvider _resolver;

        [TestFixtureSetUp]
        public void TestFixtureSetUp()
        {
            _resolver = Application.Boot(new string[0]);
        }

        [TestFixtureTearDown]
        public void TestFixtureTearDown()
        {
            Application.ShutDown();
        }

        [Test]
        public void ShouldHaveSingletonMessageBus()
        {
            IMessageBus messageBus = _resolver.GetService(typeof(IMessageBus)) as IMessageBus;
            Assert.IsNotNull(messageBus);
            IMessageBus messageBus2 = _resolver.GetService(typeof(IMessageBus)) as IMessageBus;
            Assert.IsTrue(ReferenceEquals(messageBus, messageBus2), "IMessageBus was not singleton");
        }

        [Test]
        public void ShouldNotHaveDiagnosticsService()
        {
            Assert.Catch(
                () => { _resolver.GetService(typeof(IDiagnosticsService)); });
        }

        [Test]
        public void ShouldNotHaveRecentActivityService()
        {
            Assert.Catch(
                () => { _resolver.GetService(typeof(IRecentActivityService)); });
        }

        [Test]
        [Category("Integration")]
        public void ShouldUseSameInstanceForIQueueManagerAndQueueManager()
        {
            Assert.IsTrue(object.ReferenceEquals(
                _resolver.GetService(typeof(IQueueManager)),
                _resolver.GetService(typeof(QueueManager))), "should be the same instance");
        }
    }
}