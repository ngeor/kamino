using System.Configuration;
using System.Net.Http;
using System.Threading.Tasks;
using System.Web.Http;

namespace BuzzStats.ParserWebApi
{
    public class StoryController : ApiController
    {
        // GET api/story/42
        public async Task<Story> Get(int id)
        {
            Parser parser = new Parser();
            HttpClient client = new HttpClient();
            string storyPageContents =
                await client.GetStringAsync(ConfigurationManager.AppSettings["BuzzServerUrl"] + "story.php?id=" + id);
            return parser.ParseStoryPage(storyPageContents, id);
        }
    }
}