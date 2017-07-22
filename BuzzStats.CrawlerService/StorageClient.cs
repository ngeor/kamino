using System;
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
            await client.PostAsJsonAsync(StorageWebApiUrl() + "/api/story", story);
        }
        
        private static string StorageWebApiUrl() =>
            Environment.GetEnvironmentVariable("STORAGE_WEB_API_URL") ??
            ConfigurationManager.AppSettings["StorageWebApiUrl"];

    }
}