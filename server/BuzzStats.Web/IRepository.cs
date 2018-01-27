using System;
using System.Collections.Generic;
using System.Text;
using System.Threading.Tasks;
using BuzzStats.WebApi.DTOs;

namespace BuzzStats.Web
{
    public interface IRepository
    {
        Task<IEnumerable<RecentActivity>> GetRecentActivity();
        Task<IEnumerable<StoryWithRecentComments>> GetStoriesWithRecentComments();
    }
}
