using System.Reflection;
using log4net;
using BuzzStats.Crawl;
using BuzzStats.Data;

namespace BuzzStats.Services
{
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

        public RecentActivity[] GetRecentActivity()
        {
            return WarmCache.GetRecentActivity();
        }

        #endregion
    }
}