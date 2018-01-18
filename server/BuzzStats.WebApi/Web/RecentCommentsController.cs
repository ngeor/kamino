using System.Collections.Generic;
using System.Linq;
using System.Web.Http;
using System.Web.Http.Cors;
using AutoMapper;
using BuzzStats.WebApi.DTOs;
using BuzzStats.WebApi.Storage;

namespace BuzzStats.WebApi.Web
{
    [EnableCors("*", "*", "*")]
    public class RecentCommentsController : ApiController
    {
        private readonly IStorageClient _storageClient;
        private readonly IMapper _mapper;

        public RecentCommentsController(IStorageClient storageClient, IMapper mapper)
        {
            _storageClient = storageClient;
            _mapper = mapper;
        }

        // GET api/recentcomments 
        public IEnumerable<StoryWithRecentComments> Get()
        {
            var commentWithStories = _storageClient.GetRecentComments();
            var groups = commentWithStories.GroupBy(c => c.StoryId);
            return groups.Select(g => new StoryWithRecentComments
            {
                StoryId = g.Key,
                Title = g.First().Title,
                Comments = g.Select(c => _mapper.Map<RecentComment>(c)).ToArray()
            });
        }
    }
}