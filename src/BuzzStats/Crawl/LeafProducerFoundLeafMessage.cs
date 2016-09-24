namespace BuzzStats.Crawl
{
    public class LeafProducerFoundLeafMessage
    {
        public LeafProducerFoundLeafMessage(ILeaf leaf)
        {
            Leaf = leaf;
        }

        public ILeaf Leaf { get; private set; }

        public override string ToString()
        {
            return string.Format("[LeafProducerFoundLeafMessage: Leaf={0}]", Leaf);
        }

        protected bool Equals(LeafProducerFoundLeafMessage other)
        {
            return Equals(Leaf, other.Leaf);
        }

        public override bool Equals(object obj)
        {
            if (ReferenceEquals(null, obj)) return false;
            if (ReferenceEquals(this, obj)) return true;
            if (obj.GetType() != GetType()) return false;
            return Equals((LeafProducerFoundLeafMessage) obj);
        }

        public override int GetHashCode()
        {
            return (Leaf != null ? Leaf.GetHashCode() : 0);
        }
    }
}
