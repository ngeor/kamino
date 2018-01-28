using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using AutoMapper;
using BuzzStats.Web.Mongo;
using Microsoft.AspNetCore.Cors;
using Microsoft.AspNetCore.Mvc;

namespace BuzzStats.Web.Controllers
{
    [EnableCors("Default")]
    [Route("api/[controller]")]
    public class RecentCommentsController : ControllerBase
    {
        private readonly IRepository _storageClient;

        public RecentCommentsController(IRepository storageClient)
        {
            _storageClient = storageClient;
        }

        // GET api/recentcomments 
        public async Task<IEnumerable<StoryWithRecentComments>> Get()
        {
            return await _storageClient.GetStoriesWithRecentComments();
        }
    }
}