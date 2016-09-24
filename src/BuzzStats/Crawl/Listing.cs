using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using log4net;
using NGSoftware.Common.Collections;
using BuzzStats.Common;
using BuzzStats.Data;
using BuzzStats.Downloader;
using BuzzStats.Parsing;

namespace BuzzStats.Crawl
{
    public class Listing : ILeafSource
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof (Listing));
        private readonly IDownloaderService _downloader;
        private readonly IUrlProvider _urlProvider;
        private readonly IDbContext _dbContext;

        public Listing(
            IDownloaderService downloader,
            IUrlProvider urlProvider,
            string url,
            IDbContext dbContext)
        {
            if (downloader == null)
            {
                throw new ArgumentNullException("downloader");
            }

            _downloader = downloader;

            if (urlProvider == null)
            {
                throw new ArgumentNullException("urlProvider");
            }

            _urlProvider = urlProvider;

            SourceId = url;
            Url = new Uri(url);

            if (dbContext == null)
            {
                throw new ArgumentNullException("dbContext");
            }

            _dbContext = dbContext;
        }

        public string SourceId { get; private set; }
        public Uri Url { get; private set; }

        public IEnumerable<ISource> GetChildren()
        {
            try
            {
                var stories = _downloader.DownloadStories(Url.ToString());
                Log.DebugFormat(
                    "Listing {0} found these stories before filtering: {1}",
                    Url,
                    stories.Select(sl => sl.StoryId).ToArrayString());

                using (IDbSession dbSession = _dbContext.OpenSession())
                {
                    return stories
                        .Where(s => ShouldPickStory(dbSession, s))
                        .Select(s => new StoryLeaf(_urlProvider.StoryUrl(s.StoryId), s.StoryId, this))
                        .ToArray();
                }
            }
            catch (WebException ex)
            {
                Log.Error(string.Format("Could not download {0}", Url), ex);
                return Enumerable.Empty<ISource>();
            }
        }

        public override bool Equals(object obj)
        {
            if (obj == null)
                return false;
            if (ReferenceEquals(this, obj))
                return true;
            if (obj.GetType() != typeof (Listing))
                return false;
            Listing other = (Listing) obj;
            return Url == other.Url;
        }

        public override int GetHashCode()
        {
            return Url.GetHashCode();
        }

        public override string ToString()
        {
            return string.Format("[Listing: Url={0}]", Url);
        }

        private bool ShouldPickStory(IDbSession dbSession, StoryListingSummary storyListingSummary)
        {
            var dbStory = dbSession.Stories.Read(storyListingSummary.StoryId);
            return dbStory == null || dbStory.VoteCount != storyListingSummary.VoteCount;
        }
    }
}
