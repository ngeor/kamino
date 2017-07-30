using System;
using NGSoftware.Common.Configuration;

namespace BuzzStats.WebApi.Parsing
{
    public class UrlProvider : IUrlProvider
    {
        private readonly IAppSettings _appSettings;

        public UrlProvider(IAppSettings appSettings)
        {
            _appSettings = appSettings;
        }

        public string ListingUrl(StoryListing storyListing, int page)
        {
            string path;
            switch (storyListing)
            {
                case StoryListing.Home:
                    path = "";
                    break;
                case StoryListing.Upcoming:
                    path = "upcoming.php";
                    break;
                case StoryListing.EnglishUpcoming:
                    path = "enupc.php";
                    break;
                case StoryListing.Tech:
                    path = "tech.php";
                    break;
                default:
                    throw new ArgumentOutOfRangeException(nameof(storyListing));
            }

            if (page >= 1)
            {
                path = path + "?page=" + (page + 1);
            }

            return _appSettings["BuzzServerUrl"] + path;
        }

        public string StoryUrl(int storyId)
        {
            return _appSettings["BuzzServerUrl"] + "story.php?id=" + storyId;
            ;
        }
    }
}