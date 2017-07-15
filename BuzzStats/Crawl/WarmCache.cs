using System;
using log4net;
using BuzzStats.Data;

namespace BuzzStats.Crawl
{
    class WarmCache : IWarmCache
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof(WarmCache));
        private RecentActivity[] _recentActivity;

        public WarmCache(IApiService apiService)
        {
            ApiService = apiService;
        }

        private IApiService ApiService { get; set; }

        public RecentActivity[] GetRecentActivity()
        {
            if (_recentActivity == null)
            {
                UpdateRecentActivity();
            }

            return _recentActivity;
        }

        public void UpdateRecentActivity()
        {
            try
            {
                var result = ApiService.GetRecentActivity(null);
                _recentActivity = result;
            }
            catch (Exception ex)
            {
                Log.Warn("Could not update recent activity", ex);
            }
        }
    }
}