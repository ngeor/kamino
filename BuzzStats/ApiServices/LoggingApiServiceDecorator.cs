//
//  LoggingApiServiceDecorator.cs
//
//  Author:
//       ngeor
//
//  Copyright (c) 2014 ngeor

using System;
using log4net;
using BuzzStats.Data;

namespace BuzzStats.ApiServices
{
    public class LoggingApiServiceDecorator : IApiService
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof(LoggingApiServiceDecorator));

        public LoggingApiServiceDecorator(IApiService backend)
        {
            if (backend == null)
            {
                throw new ArgumentNullException("backend");
            }

            Backend = backend;
        }

        public IApiService Backend { get; private set; }

        #region IApiService implementation

        public CountStatsResponse GetCommentCountStats(CountStatsRequest request)
        {
            Log.Debug("GetCommentCountStats begin");
            var result = Backend.GetCommentCountStats(request);
            Log.Debug("GetCommentCountStats end");
            return result;
        }

        public HostStats[] GetHostStats(HostStatsRequest request)
        {
            Log.Debug("GetHostStats begin");
            var result = Backend.GetHostStats(request);
            Log.Debug("GetHostStats end");
            return result;
        }

        public RecentActivity[] GetRecentActivity(RecentActivityRequest request)
        {
            Log.Debug("GetRecentActivity begin");
            var result = Backend.GetRecentActivity(request);
            Log.Debug("GetRecentActivity end");
            return result;
        }

        public RecentlyCommentedStory[] GetRecentCommentsPerStory()
        {
            Log.Debug("GetRecentCommentsPerStory begin");
            var result = Backend.GetRecentCommentsPerStory();
            Log.Debug("GetRecentCommentsPerStory end");
            return result;
        }

        public CommentSummary[] GetRecentPopularComments()
        {
            Log.Debug("GetRecentPopularComments begin");
            var result = Backend.GetRecentPopularComments();
            Log.Debug("GetRecentPopularComments end");
            return result;
        }

        public StorySummary[] GetStorySummaries(GetStorySummariesRequest request)
        {
            Log.Debug("GetStorySummaries begin");
            var result = Backend.GetStorySummaries(request);
            Log.Debug("GetStorySummaries end");
            return result;
        }

        public UserStats[] GetUserStats(UserStatsRequest request)
        {
            Log.Debug("GetUserStats begin");
            var result = Backend.GetUserStats(request);
            Log.Debug("GetUserStats end");
            return result;
        }

        public CountStatsResponse GetStoryCountStats(CountStatsRequest request)
        {
            Log.Debug("GetStoryCountStats begin");
            var result = Backend.GetStoryCountStats(request);
            Log.Debug("GetStoryCountStats end");
            return result;
        }

        #endregion
    }
}