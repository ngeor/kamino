using System.Configuration;
using System.Net.Http;
using System.Threading.Tasks;
using System.Web.Http;
using BuzzStats.WebApi.DTOs;
using log4net;

namespace BuzzStats.WebApi.Parsing
{
    public class StoryController : ApiController
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof(StoryController));
        
        // GET api/story/42
        public async Task<Story> Get(int id)
        {
            Log.InfoFormat("/api/story/{0}", id);
            Parser parser = new Parser();
            HttpClient client = new HttpClient();
            string storyPageContents =
                await client.GetStringAsync(ConfigurationManager.AppSettings["BuzzServerUrl"] + "story.php?id=" + id);
            return parser.ParseStoryPage(storyPageContents, id);
        }
    }
}