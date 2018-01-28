using MongoDB.Bson;

namespace BuzzStats.Web.Mongo
{
    public class StoryWithRecentComments
    {
        public ObjectId Id { get; set; }
        public int StoryId { get; set; }
        public string Title { get; set; }
        public RecentComment[] Comments { get; set; }
    }
}