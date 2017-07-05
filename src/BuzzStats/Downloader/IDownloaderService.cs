using System.ServiceModel;
using BuzzStats.Parsing;

namespace BuzzStats.Downloader
{
    [ServiceContract]
    public interface IDownloaderService
    {
        [OperationContract]
        Story DownloadStory(string url, int storyId);

        [OperationContract]
        StoryListingSummary[] DownloadStories(string url);
    }
}