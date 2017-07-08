using System;
using System.Collections.Generic;
using System.Web.Http;
using log4net;

namespace BuzzStats.StorageWebApi
{
    public class StoryController : ApiController
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof(StoryController));
        
        // GET api/story 
        public IEnumerable<string> Get()
        {
            return new[] {"value1", "value2"};
        }

        // GET api/story/5 
        public string Get(int id)
        {
            return "value";
        }

        // POST api/story 
        public void Post([FromBody] Story value)
        {
            Log.InfoFormat("Received story {0} title {1}", value.StoryId, value.Title);
        }

        // PUT api/story/5 
        public void Put(int id, [FromBody] string value)
        {
        }

        // DELETE api/story/5 
        public void Delete(int id)
        {
        }
    }
    
    
    public class Story
    {
        public int StoryId { get; set; }

        public string Title { get; set; }

        public bool IsRemoved { get; set; }

        public int Category { get; set; }

        public string Url { get; set; }

        public DateTime CreatedAt { get; set; }

        public string Username { get; set; }

        public string[] Voters { get; set; }

        public Comment[] Comments { get; set; }
    }
    
    public class Comment
    {
        public int CommentId { get; set; }

        public string Username { get; set; }

        public DateTime CreatedAt { get; set; }

        public int VotesUp { get; set; }

        public int VotesDown { get; set; }

        public bool IsBuried { get; set; }

        public Comment[] Comments { get; set; }
    }
    
}