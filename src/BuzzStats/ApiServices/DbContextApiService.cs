// --------------------------------------------------------------------------------
// <copyright file="DbContextApiService.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/11/24
// * Time: 17:15:42
// --------------------------------------------------------------------------------

using System;
using BuzzStats.Data;

namespace BuzzStats.ApiServices
{
    public class DbContextApiService : IApiService
    {
        public DbContextApiService(IDbContext dbContext)
        {
            DbContext = dbContext;
        }

        private IDbContext DbContext { get; set; }

        private TResult Wrap<TResult>(Func<IApiService, TResult> func)
        {
            using (var session = DbContext.OpenSession())
            {
                return func(new ApiService(session));
            }
        }

        #region IApiService implementation

        public CountStatsResponse GetCommentCountStats(CountStatsRequest request)
        {
            throw new NotImplementedException();
        }

        public HostStats[] GetHostStats(HostStatsRequest request)
        {
            throw new NotImplementedException();
        }

        public RecentActivity[] GetRecentActivity(RecentActivityRequest request)
        {
            return Wrap(session => session.GetRecentActivity(request));
        }

        public RecentlyCommentedStory[] GetRecentCommentsPerStory()
        {
            throw new NotImplementedException();
        }

        public CommentSummary[] GetRecentPopularComments()
        {
            throw new NotImplementedException();
        }

        public StorySummary[] GetStorySummaries(GetStorySummariesRequest request)
        {
            throw new NotImplementedException();
        }

        public UserStats[] GetUserStats(UserStatsRequest request)
        {
            throw new NotImplementedException();
        }

        public CountStatsResponse GetStoryCountStats(CountStatsRequest request)
        {
            throw new NotImplementedException();
        }

        #endregion
    }
}
