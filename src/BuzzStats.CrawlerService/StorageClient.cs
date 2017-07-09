using System.Configuration;
using System.Net.Http;
using System.Threading.Tasks;
using BuzzStats.CrawlerService.DTOs;

namespace BuzzStats.CrawlerService
{
    public class StorageClient
    {
        public virtual async Task Save(Story story)
        {
            HttpClient client = new HttpClient();
            await client.PostAsJsonAsync(ConfigurationManager.AppSettings["StorageWebApiUrl"] + "/api/story", story);
        }
    }
}