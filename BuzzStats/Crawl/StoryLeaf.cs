// --------------------------------------------------------------------------------
// <copyright file="StorySource.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/10/17
// * Time: 04:51:16
// --------------------------------------------------------------------------------

using System;
using System.Collections.Generic;
using System.Net;
using log4net;
using NGSoftware.Common.Messaging;
using BuzzStats.Downloader;
using BuzzStats.Parsing;

namespace BuzzStats.Crawl
{
    public class StoryLeaf : ILeaf
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof(StoryLeaf));

        public StoryLeaf(string url, int storyId, ILeafSource leafSource)
        {
            Url = new Uri(url);
            StoryId = storyId;
            LeafSource = leafSource;
        }

        public int StoryId { get; private set; }

        public Uri Url { get; private set; }

        public ILeafSource LeafSource { get; private set; }

        public IEnumerable<ISource> GetChildren()
        {
            throw new NotSupportedException();
        }

        public void Update(IDownloaderService downloader, IMessageBus messageBus)
        {
            Story story = null;
            try
            {
                story = downloader.DownloadStory(Url.ToString(), StoryId);
                if (story == null)
                {
                    Log.ErrorFormat("Downloader returned a null story {0} url {1}", StoryId, Url);
                }
            }
            catch (WebException ex)
            {
                Log.Error(string.Format("Error downloading story {0} url {1}", StoryId, Url), ex);
            }

            if (story != null)
            {
                messageBus.Publish(new StoryDownloadedMessage(story, LeafSource));
            }
        }

        public override bool Equals(object obj)
        {
            if (obj == null)
                return false;
            if (ReferenceEquals(this, obj))
                return true;
            if (obj.GetType() != typeof(StoryLeaf))
                return false;
            StoryLeaf other = (StoryLeaf) obj;
            return StoryId == other.StoryId && Url == other.Url;
        }

        public override int GetHashCode()
        {
            unchecked
            {
                int result = StoryId.GetHashCode();
                result = result * 11 + Url.GetHashCode();
                return result;
            }
        }

        public override string ToString()
        {
            return string.Format("[StoryLeaf: StoryId={0}, Url={1}, LeafSource={2}]", StoryId, Url, LeafSource);
        }
    }
}