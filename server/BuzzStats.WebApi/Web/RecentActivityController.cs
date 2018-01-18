using System.Collections.Generic;
using System.Web.Http;
using BuzzStats.WebApi.DTOs;
using BuzzStats.WebApi.Storage;

namespace BuzzStats.WebApi.Web
{
    /// <summary>
    /// Controller for recent activities.
    /// </summary>
    public class RecentActivityController : ApiController
    {
        private readonly IStorageClient _storageClient;

        public RecentActivityController(IStorageClient storageClient)
        {
            _storageClient = storageClient;
        }

        // GET api/recentactivity
        public IEnumerable<RecentActivity> Get()
        {
            return _storageClient.GetRecentActivity();
        }
    }
}
