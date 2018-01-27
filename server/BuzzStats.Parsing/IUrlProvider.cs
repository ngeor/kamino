namespace BuzzStats.Parsing
{
    public interface IUrlProvider
    {
        string ListingUrl(StoryListing storyListing, int page = 1);
        string StoryUrl(int storyId, int? commentId = null);
    }
}