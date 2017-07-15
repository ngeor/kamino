using System;
using BuzzStats.Data;
using BuzzStats.Persister;

namespace BuzzStats.Crawl
{
    public class StoryCheckedMessage
    {
        public StoryCheckedMessage(StoryData story, ILeafSource leafSource, UpdateResult changes)
        {
            if (story == null)
            {
                throw new ArgumentNullException("story");
            }

            if (leafSource == null)
            {
                throw new ArgumentNullException("leafSource");
            }

            Story = story;
            LeafSource = leafSource;
            Changes = changes;
        }

        public StoryData Story { get; private set; }

        public ILeafSource LeafSource { get; private set; }

        public UpdateResult Changes { get; private set; }

        public override string ToString()
        {
            return string.Format("[StoryCheckedMessage: Story={0}, LeafSource={1}, Changes={2}]", Story, LeafSource,
                Changes);
        }

        public override bool Equals(object obj)
        {
            if (obj == null)
                return false;
            if (ReferenceEquals(this, obj))
                return true;
            if (obj.GetType() != typeof(StoryCheckedMessage))
                return false;
            StoryCheckedMessage other = (StoryCheckedMessage) obj;
            return Story.Equals(other.Story) && LeafSource.Equals(other.LeafSource) && Changes == other.Changes;
        }

        public override int GetHashCode()
        {
            unchecked
            {
                return Story.GetHashCode() ^ LeafSource.GetHashCode() ^ Changes.GetHashCode();
            }
        }
    }
}