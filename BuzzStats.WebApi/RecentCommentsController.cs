using System.Collections.Generic;
using System.Web.Http;
using BuzzStats.WebApi.DTOs;

namespace BuzzStats.WebApi
{
    public class RecentCommentsController : ApiController
    {
        // GET api/recentcomments 
        public IEnumerable<StoryWithRecentComments> Get()
        {
            return new[]
            {
                new StoryWithRecentComments
                {
                    StoryId = 42,
                    Title = "hello",
                    Comments = new[]
                    {
                        new RecentComment
                        {
                            CommentId = 1,
                            User = "hello again",
                            VotesUp = 1
                        }
                    }
                }
            };
        }
    }
}