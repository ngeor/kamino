using System.Collections.Generic;
using NGSoftware.Common.Messaging;

namespace BuzzStats.Crawl
{
    public class LeafProducerMonitor : ILeafProducerMonitor
    {
        private List<ILeaf> _leaves = new List<ILeaf>();

        public LeafProducerMonitor(IMessageBus messageBus)
        {
            messageBus.Subscribe<LeafProducerFoundLeafMessage>(OnFoundLeaf);
            messageBus.Subscribe<LeafProducerFinishedMessage>(OnFinished);
        }

        private void OnFinished(LeafProducerFinishedMessage obj)
        {
            _leaves = new List<ILeaf>();
        }

        private void OnFoundLeaf(LeafProducerFoundLeafMessage message)
        {
            _leaves.Add(message.Leaf);
        }

        public ILeaf[] GetCollectedLeaves()
        {
            return _leaves.ToArray();
        }
    }
}