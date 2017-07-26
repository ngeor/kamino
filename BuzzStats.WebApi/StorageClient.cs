using System.Collections.Generic;
using System.Net.Http;
using System.Threading.Tasks;
using BuzzStats.WebApi.DTOs;
using log4net;
using Newtonsoft.Json;
using NGSoftware.Common.Configuration;

namespace BuzzStats.WebApi
{
    public class StorageClient
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof(StorageClient));
        
        private readonly IAppSettings _appSettings;

        public StorageClient(IAppSettings appSettings)
        {
            _appSettings = appSettings;
        }
        
        public virtual async Task<IEnumerable<CommentWithStory>> GetComments()
        {
            var requestUri = StorageWebApiUrl() + "/api/comment";
            Log.InfoFormat("Calling {0}", requestUri);
            HttpClient client = new HttpClient();
            var jsonString = await client.GetStringAsync(requestUri);
            return JsonConvert.DeserializeObject<IEnumerable<CommentWithStory>>(jsonString);
        }

        private string StorageWebApiUrl() => _appSettings["StorageWebApiUrl"];
    }
}