// --------------------------------------------------------------------------------
// <copyright file="DownloaderService.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/05/30
// * Time: 07:57:38
// --------------------------------------------------------------------------------

using System.Linq;
using System.ServiceModel;
using log4net;
using NGSoftware.Common.Net;
using BuzzStats.Parsing;

namespace BuzzStats.Downloader
{
    [ServiceBehavior(InstanceContextMode = InstanceContextMode.Single)]
    public class DownloaderService : IDownloaderService
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof(DownloaderService));
        private readonly IDownloader _downloader;
        private readonly IParser _parser;

        public DownloaderService(IDownloader downloader, IParser parser)
        {
            _downloader = downloader;
            _parser = parser;
        }

        public string Download(string url)
        {
            Log.DebugFormat("Begin download {0}", url);
            string html = _downloader.Download(url);
            Log.DebugFormat("Finished download {0}", url);
            return html;
        }

        public Story DownloadStory(string url, int storyId)
        {
            string html = Download(url);
            Story story = _parser.ParseStoryPage(html, storyId);
            Log.DebugFormat("Requested story {0}, returning {1}", storyId, story.StoryId);
            return story;
        }

        public StoryListingSummary[] DownloadStories(string url)
        {
            string html = Download(url);
            return _parser.ParseListingPage(html).ToArray();
        }
    }
}