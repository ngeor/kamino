using System.Collections.Generic;
using System.Threading.Tasks;
using BuzzStats.Web.Mongo;
using Microsoft.AspNetCore.Cors;
using Microsoft.AspNetCore.Mvc;

namespace BuzzStats.Web.Controllers
{
    /// <summary>
    /// Controller for recent activities.
    /// </summary>
    [EnableCors("Default")]
    [Route("api/[controller]")]
    public class RecentActivityController : ControllerBase
    {
        private readonly IRepository _storageClient;

        public RecentActivityController(IRepository storageClient)
        {
            _storageClient = storageClient;
        }

        // GET api/recentactivity
        public async Task<IEnumerable<RecentActivity>> Get()
        {
            return await _storageClient.GetRecentActivity();
        }
    }
}
