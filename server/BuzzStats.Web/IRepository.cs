using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using BuzzStats.Web.DTOs;

namespace BuzzStats.Web
{
    public interface IRepository
    {
        Task<IEnumerable<RecentActivity>> GetRecentActivity();
        Task<IEnumerable<StoryWithRecentComments>> GetStoriesWithRecentComments();
        Task Save(RecentActivity recentActivity);
    }
}
