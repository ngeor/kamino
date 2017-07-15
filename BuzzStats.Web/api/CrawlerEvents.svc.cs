using System;
using System.ServiceModel.Activation;
using log4net;
using Microsoft.Practices.ServiceLocation;
using NGSoftware.Common.Cache;
using BuzzStats.ApiServices;

namespace BuzzStats.Web.api
{
    [AspNetCompatibilityRequirements(RequirementsMode = AspNetCompatibilityRequirementsMode.Allowed)]
    public class CrawlerEvents : ICrawlerEvents
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof(CrawlerEvents));

        public void StoryChecked(StoryCheckedEventArgs arguments)
        {
            Log.DebugFormat("StoryChecked Story={0} HadChanges={1}", arguments.StoryId, arguments.HadChanges);
            MessageStack.Instance.Latest = arguments;

            if (arguments.HadChanges)
            {
                InvalidateMasterCache();
            }
        }

        private void InvalidateMasterCache()
        {
            ICache cache = ServiceLocator.Current.GetInstance<ICache>();
            cache.Remove(CachedApiService.MasterKey);
        }
    }

    class MessageStack
    {
        private MessageStack()
        {
        }

        private static readonly Lazy<MessageStack> _instance = new Lazy<MessageStack>(() => new MessageStack());

        public static MessageStack Instance
        {
            get { return _instance.Value; }
        }

        public StoryCheckedEventArgs Latest { get; set; }
    }
}