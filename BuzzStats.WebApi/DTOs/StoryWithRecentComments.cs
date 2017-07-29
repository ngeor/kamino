namespace BuzzStats.WebApi.DTOs
{
    public class StoryWithRecentComments
    {
        public int StoryId { get; set; }
        public string Title { get; set; }
        public RecentComment[] Comments { get; set; }
    }
}