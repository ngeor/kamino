using System;

namespace BuzzStats.Parsing
{
    public class UrlProvider : IUrlProvider
    {
        private readonly string _buzzStatsUrl;

        public UrlProvider(string buzzStatsUrl)
        {
            _buzzStatsUrl = buzzStatsUrl;
        }

        public string ListingUrl(StoryListing storyListing, int page = 0)
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

            return _buzzStatsUrl + path;
        }

        public string StoryUrl(int storyId, int? commentId = null)
        {
            var result = _buzzStatsUrl + "story.php?id=" + storyId;
            if (commentId.HasValue)
            {
                result += "#wholecomment" + commentId.Value;
            }

            return result;
        }
    }
}