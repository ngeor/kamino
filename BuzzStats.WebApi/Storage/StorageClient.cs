using System.Net.Http;
using System.Threading.Tasks;
using BuzzStats.WebApi.DTOs;
using log4net;
using NGSoftware.Common.Configuration;

namespace BuzzStats.WebApi.Storage
{
    public class StorageClient : IStorageClient
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof(StorageClient));
        
        private readonly IAppSettings _appSettings;

        public StorageClient(IAppSettings appSettings)
        {
            _appSettings = appSettings;
        }
        
        public virtual async Task Save(Story story)
        {
            var requestUri = StorageWebApiUrl() + "/api/story";
            Log.InfoFormat("Calling {0}", requestUri);
            HttpClient client = new HttpClient();
            await client.PostAsJsonAsync(requestUri, story);
        }

        private string StorageWebApiUrl() => _appSettings["StorageWebApiUrl"];
    }
}