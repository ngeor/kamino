using System.Runtime.Serialization;

namespace BuzzStats.Web.api
{
    [DataContract]
    public class StoryCheckedEventArgs
    {
        [DataMember]
        public int StoryId { get; set; }

        [DataMember]
        public bool HadChanges { get; set; }

        [DataMember]
        public string SelectorName { get; set; }

        public override string ToString()
        {
            return string.Format("{0} HadChanges={1} SelectorName={2}", base.ToString(), HadChanges, SelectorName);
        }

        public override bool Equals(object obj)
        {
            if (obj == null)
                return false;
            if (ReferenceEquals(this, obj))
                return true;
            if (obj.GetType() != typeof (StoryCheckedEventArgs))
                return false;
            StoryCheckedEventArgs other = (StoryCheckedEventArgs) obj;
            return StoryId == other.StoryId && HadChanges == other.HadChanges &&
                string.Equals(SelectorName, other.SelectorName);
        }

        public override int GetHashCode()
        {
            unchecked
            {
                int result = StoryId.GetHashCode();
                result = result*11 + HadChanges.GetHashCode();
                result = result*13 + (SelectorName != null ? SelectorName.GetHashCode() : 0);
                return result;
            }
        }
    }
}
