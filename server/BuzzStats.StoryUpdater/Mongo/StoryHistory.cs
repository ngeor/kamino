using MongoDB.Bson;
using System;

namespace BuzzStats.StoryUpdater.Mongo
{
    public class StoryHistory
    {
        public ObjectId Id { get; set; }
        public int StoryId { get; set; }
        public DateTime LastModifiedAt { get; set; }
        public DateTime LastCheckedAt { get; set; }
    }
}
