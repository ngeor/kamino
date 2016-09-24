// --------------------------------------------------------------------------------
// <copyright file="CachedApiService.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/10/04
// * Time: 10:11 μμ
// --------------------------------------------------------------------------------

using NGSoftware.Common.Cache;
using NGSoftware.Common.Factories;
using BuzzStats.Data;

namespace BuzzStats.ApiServices
{
    public class CachedApiService : CacheBase<IApiService>, IApiService
    {
        public const string MasterKey = "CachedApiService-masterkey";

        public CachedApiService(ICache cache, IFactory<IApiService> backendFactory)
            : base(cache, backendFactory)
        {
        }

        public CountStatsResponse GetCommentCountStats(CountStatsRequest request)
        {
            return Backend.GetCommentCountStats(request);
        }

        public HostStats[] GetHostStats(HostStatsRequest request)
        {
            return Backend.GetHostStats(request);
        }

        public RecentActivity[] GetRecentActivity(RecentActivityRequest request)
        {
            string cacheKey = "IApiService.GetRecentActivity";
            if (request != null)
            {
                cacheKey += request.ToString();
            }

            return Cache(cacheKey, () => Backend.GetRecentActivity(request), MasterKey);
        }

        public RecentlyCommentedStory[] GetRecentCommentsPerStory()
        {
            string cacheKey = "IApiService.GetRecentCommentsPerStory";
            return Cache(cacheKey, () => Backend.GetRecentCommentsPerStory(), MasterKey);
        }

        public CommentSummary[] GetRecentPopularComments()
        {
            return Cache("IApiService.GetRecentPopularComments", () => Backend.GetRecentPopularComments(), MasterKey);
        }

        public StorySummary[] GetStorySummaries(GetStorySummariesRequest request)
        {
            string cacheKey = "IApiService.GetStorySummaries";
            if (request != null)
            {
                cacheKey += request.ToString();
            }

            return Cache(cacheKey, () => Backend.GetStorySummaries(request), MasterKey);
        }

        public UserStats[] GetUserStats(UserStatsRequest request)
        {
            return Backend.GetUserStats(request);
        }

        public CountStatsResponse GetStoryCountStats(CountStatsRequest request)
        {
            return Backend.GetStoryCountStats(request);
        }
    }
}
