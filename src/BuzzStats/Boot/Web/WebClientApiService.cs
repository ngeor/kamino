using System;
using log4net;
using BuzzStats.Data;
using BuzzStats.Services;

namespace BuzzStats.Boot.Web
{
    public class WebClientApiService : IApiService
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof(WebClientApiService));
        private readonly IApiService _backend;
        private readonly IRecentActivityService _recentActivityService;

        public WebClientApiService(IApiService apiService, IRecentActivityService recentActivityService)
        {
            _backend = apiService;
            _recentActivityService = recentActivityService;
        }

        public CountStatsResponse GetCommentCountStats(CountStatsRequest request)
        {
            return _backend.GetCommentCountStats(request);
        }

        public HostStats[] GetHostStats(HostStatsRequest request)
        {
            return _backend.GetHostStats(request);
        }

        public RecentActivity[] GetRecentActivity(RecentActivityRequest request)
        {
            if (request != null)
            {
                return _backend.GetRecentActivity(request);
            }

            try
            {
                return _recentActivityService.GetRecentActivity();
            }
            catch (Exception ex)
            {
                Log.Error("Error getting recent activity", ex);
                return new RecentActivity[0];
            }
        }

        public RecentlyCommentedStory[] GetRecentCommentsPerStory()
        {
            return _backend.GetRecentCommentsPerStory();
        }

        public CommentSummary[] GetRecentPopularComments()
        {
            return _backend.GetRecentPopularComments();
        }

        public StorySummary[] GetStorySummaries(GetStorySummariesRequest request)
        {
            return _backend.GetStorySummaries(request);
        }

        public UserStats[] GetUserStats(UserStatsRequest request)
        {
            return _backend.GetUserStats(request);
        }

        public CountStatsResponse GetStoryCountStats(CountStatsRequest request)
        {
            return _backend.GetStoryCountStats(request);
        }
    }
}