using MongoDB.Bson;

namespace BuzzStats.Web.DTOs
{
    public class StoryWithRecentComments
    {
        public ObjectId Id { get; set; }
        public int StoryId { get; set; }
        public string Title { get; set; }
        public RecentComment[] Comments { get; set; }
    }
}