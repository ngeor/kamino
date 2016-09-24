// --------------------------------------------------------------------------------
// <copyright file="IDownloaderService.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/05/30
// * Time: 07:57:30
// --------------------------------------------------------------------------------

using System.ServiceModel;
using BuzzStats.Parsing;

namespace BuzzStats.Downloader
{
    [ServiceContract]
    public interface IDownloaderService
    {
        [OperationContract]
        string Download(string url);

        [OperationContract]
        Story DownloadStory(string url, int storyId);

        [OperationContract]
        StoryListingSummary[] DownloadStories(string url);
    }
}
