using System;
using System.Collections.Generic;
using System.Configuration;
using System.Net.Http;
using System.Threading.Tasks;
using System.Web.Http;
using BuzzStats.WebApi.DTOs;
using log4net;

namespace BuzzStats.WebApi.Parsing
{
    public class ListingController : ApiController
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof(ListingController));
        
        // GET api/listing/home 
        public async Task<IEnumerable<StoryListingSummary>> Get(StoryListing id)
        {
            Log.InfoFormat("/api/listing/{0}", id);
            Parser parser = new Parser();
            HttpClient client = new HttpClient();
            string path;
            switch (id)
            {
                case StoryListing.Home:
                    path = "";
                    break;
                case StoryListing.Upcoming:
                    path = "upcoming.php";
                    break;
                default:
                    path = "";
                    break;
            }

            var requestUri = ConfigurationManager.AppSettings["BuzzServerUrl"] + path;
            Log.InfoFormat("Calling {0}", requestUri);
            try
            {
                string htmlContents = await client.GetStringAsync(requestUri);
                return parser.ParseListingPage(htmlContents);
            }
            catch (Exception ex)
            {
                Log.Error(ex.Message, ex);
                throw;
            }
        }
    }
}