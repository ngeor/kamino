using System.Reflection;
using log4net;
using NGSoftware.Common.WebServices;
using BuzzStats.Crawl;
using BuzzStats.Data;

namespace BuzzStats.Services
{
    [WebService]
    public class RecentActivityService : IRecentActivityService
    {
        private static readonly ILog Log = LogManager.GetLogger(
            MethodBase.GetCurrentMethod().DeclaringType);

        public RecentActivityService(IWarmCache warmCache)
        {
            Log.DebugFormat("{0} constructor", GetType().Name);
            this.WarmCache = warmCache;
        }

        private IWarmCache WarmCache { get; set; }

        #region IRecentActivityService implementation

        [DefaultRoute]
        public RecentActivity[] GetRecentActivity()
        {
            return WarmCache.GetRecentActivity();
        }

        #endregion
    }
}