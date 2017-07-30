namespace BuzzStats.WebApi.Parsing
{
    public interface IUrlProvider
    {
        string ListingUrl(StoryListing storyListing, int page = 0);
        string StoryUrl(int storyId, int? commentId = null);
    }
}