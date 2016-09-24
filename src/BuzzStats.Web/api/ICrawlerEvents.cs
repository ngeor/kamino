using System.ServiceModel;

namespace BuzzStats.Web.api
{
    [ServiceContract]
    public interface ICrawlerEvents
    {
        [OperationContract]
        void StoryChecked(StoryCheckedEventArgs arguments);
    }
}
