using BuzzStats.DTOs;
using MongoDB.Driver;
using System;
using System.Threading.Tasks;

namespace BuzzStats.StoryUpdater
{
    class MongoRepository : IMongoRepository
    {
        private readonly string connectionString;

        public MongoRepository(string connectionString)
        {
            this.connectionString = connectionString;
        }

        public async Task RegisterChangeEvent(StoryEvent storyEvent)
        {
            IMongoCollection<StoryHistory> collection = GetCollection();
            var exists = await collection.Find(f => f.StoryId == storyEvent.StoryId).AnyAsync();
            if (exists)
            {
                await collection.FindOneAndUpdateAsync(
                    f => f.StoryId == storyEvent.StoryId,
                    Builders<StoryHistory>.Update.Set(f => f.LastModifiedAt, DateTime.UtcNow));
            }
            else
            {
                await collection.InsertOneAsync(new StoryHistory
                {
                    StoryId = storyEvent.StoryId,
                    LastCheckedAt = DateTime.MinValue,
                    LastModifiedAt = storyEvent.CreatedAt
                });
            }
        }

        private IMongoCollection<StoryHistory> GetCollection()
        {
            var mongoClient = new MongoClient(connectionString);
            var db = mongoClient.GetDatabase("StoryUpdater");
            var collection = db.GetCollection<StoryHistory>("Stories");
            return collection;
        }

        public async Task<StoryHistory> OldestCheckedStory()
        {
            var collection = GetCollection();
            return await collection.Find(Builders<StoryHistory>.Filter.Empty)
                .Sort(Builders<StoryHistory>.Sort.Ascending(f => f.LastCheckedAt))
                .FirstOrDefaultAsync();
        }

        public async Task UpdateLastCheckedDate(int storyId)
        {
            IMongoCollection<StoryHistory> collection = GetCollection();
            await collection.FindOneAndUpdateAsync(
                f => f.StoryId == storyId,
                Builders<StoryHistory>.Update.Set(f => f.LastCheckedAt, DateTime.UtcNow));
        }
    }
}
