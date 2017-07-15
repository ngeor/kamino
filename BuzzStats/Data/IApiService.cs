// --------------------------------------------------------------------------------
// <copyright file="IApiService.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/09/04
// * Time: 1:27 μμ
// --------------------------------------------------------------------------------

namespace BuzzStats.Data
{
    public interface IApiService
    {
        CountStatsResponse GetCommentCountStats(CountStatsRequest request);
        HostStats[] GetHostStats(HostStatsRequest request);
        RecentActivity[] GetRecentActivity(RecentActivityRequest request);
        RecentlyCommentedStory[] GetRecentCommentsPerStory();
        CommentSummary[] GetRecentPopularComments();
        StorySummary[] GetStorySummaries(GetStorySummariesRequest request);
        UserStats[] GetUserStats(UserStatsRequest request);
        CountStatsResponse GetStoryCountStats(CountStatsRequest request);
    }
}