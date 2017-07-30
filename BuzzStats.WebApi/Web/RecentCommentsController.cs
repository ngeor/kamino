using System.Collections.Generic;
using System.Linq;
using System.Web.Http;
using BuzzStats.WebApi.DTOs;
using BuzzStats.WebApi.Storage;

namespace BuzzStats.WebApi.Web
{
    public class RecentCommentsController : ApiController
    {
        private readonly IStorageClient _storageClient;

        public RecentCommentsController(IStorageClient storageClient)
        {
            _storageClient = storageClient;
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
                Comments = g.Select(c => new RecentComment
                {
                    CommentId = c.CommentId,
                    Username = c.Username,
                    VotesUp = c.VotesUp
                }).ToArray()
            });
        }
    }
}