using System.Linq;
using Moq;
using NUnit.Framework;
using BuzzStats.Crawl;
using BuzzStats.Tests.Utils;

namespace BuzzStats.Tests.Crawl
{
    [TestFixture]
    public class LeafProducerMonitorTest
    {
        [Test]
        public void ShouldBeEmptyAtStart()
        {
            StubMessageBus messageBus = new StubMessageBus();
            LeafProducerMonitor monitor = new LeafProducerMonitor(messageBus);
            Assert.AreEqual(0, monitor.GetCollectedLeaves().Count());
        }

        [Test]
        public void ShouldAddLeafWhenProducerNotifies()
        {
            StubMessageBus messageBus = new StubMessageBus();
            LeafProducerMonitor monitor = new LeafProducerMonitor(messageBus);
            ILeaf leaf = Mock.Of<ILeaf>();
            messageBus.Publish(new LeafProducerFoundLeafMessage(leaf));
            CollectionAssert.AreEqual(new[]
            {
                leaf
            }, monitor.GetCollectedLeaves());
        }

        [Test]
        public void ShouldClearListWhenProducerFinishes()
        {
            StubMessageBus messageBus = new StubMessageBus();
            LeafProducerMonitor monitor = new LeafProducerMonitor(messageBus);
            ILeaf leaf = Mock.Of<ILeaf>();
            messageBus.Publish(new LeafProducerFoundLeafMessage(leaf));
            messageBus.Publish(new LeafProducerFinishedMessage(new[] {leaf}));
            Assert.AreEqual(0, monitor.GetCollectedLeaves().Count());
        }
    }
}