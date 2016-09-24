using System;
using System.Collections.Generic;
using BuzzStats.Data;

namespace BuzzStats.Web.DataSources
{
    public class UserStatsDataSource
    {
        public UserStatsDataSource()
        {
            ApiService = new ConsoleService();
        }

        public IApiService ApiService { get; set; }

        public IEnumerable<UserStats> Select(string sortExpression, DateTime? startDate, DateTime? endDate)
        {
            return ApiService.GetUserStats(new UserStatsRequest
            {
                SortExpression = sortExpression,
                Start = startDate,
                Stop = endDate
            });
        }
    }
}
