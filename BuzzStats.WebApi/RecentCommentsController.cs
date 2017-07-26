using System.Collections.Generic;
using System.Web.Http;

namespace BuzzStats.WebApi
{
    public class RecentCommentsController : ApiController
    {
        // GET api/values 
        public IEnumerable<string> Get()
        {
            return new[] {"value1", "value2"};
        }
    }
}