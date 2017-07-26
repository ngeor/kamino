// --------------------------------------------------------------------------------
// <copyright file="DSLExtensions.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/10/17
// * Time: 09:36:35
// --------------------------------------------------------------------------------

using System;
using System.Linq;
using BuzzStats.Downloader;
using BuzzStats.Parsing;
using Moq;

namespace BuzzStats.UnitTests.DSL
{
    public static class DownloaderServiceDSL
    {
        public static IDownloaderService SetupDownloadStories(
            this IDownloaderService downloader,
            string url,
            params int[] storyIds)
        {
            return downloader.SetupDownloadStories(url, storyIds.Select(s => new StoryListingSummary(s)).ToArray());
        }

        public static IDownloaderService SetupDownloadStories(
            this IDownloaderService downloader,
            string url,
            params StoryListingSummary[] storyListingSummaries)
        {
            Mock.Get<IDownloaderService>(downloader)
                .Setup(p => p.DownloadStories(url))
                .Returns(storyListingSummaries);
            return downloader;
        }

        public static IDownloaderService SetupDownloadStories(
            this IDownloaderService downloader,
            string url,
            Exception exception)
        {
            Mock.Get<IDownloaderService>(downloader)
                .Setup(p => p.DownloadStories(url))
                .Throws(exception);
            return downloader;
        }

        public static DownloadStoryDSL SetupDownloadStory(this IDownloaderService downloader, string url, int storyId)
        {
            return new DownloadStoryDSL(downloader, url, storyId);
        }

        public class DownloadStoryDSL
        {
            readonly IDownloaderService downloader;
            readonly string url;
            readonly int storyId;

            public DownloadStoryDSL(IDownloaderService downloader, string url, int storyId)
            {
                this.downloader = downloader;
                this.url = url;
                this.storyId = storyId;
            }

            public IDownloaderService Returns(Story story)
            {
                Mock.Get<IDownloaderService>(downloader)
                    .Setup(p => p.DownloadStory(url, storyId))
                    .Returns(story);
                return downloader;
            }
        }
    }
}