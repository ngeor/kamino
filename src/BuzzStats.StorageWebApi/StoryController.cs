using System.Collections.Generic;
using System.Web.Http;
using BuzzStats.StorageWebApi.DTOs;
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
}