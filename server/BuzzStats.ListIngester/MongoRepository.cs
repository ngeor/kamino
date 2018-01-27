using MongoDB.Bson;
using MongoDB.Driver;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace BuzzStats.ListIngester
{
    public class MongoRepository : IMongoRepository
    {
        private readonly string connectionString;

        public MongoRepository(string connectionString)
        {
            this.connectionString = connectionString ?? throw new ArgumentNullException(nameof(connectionString));
        }

        /// <summary>
        /// Adds the given story id to the Stories collection.
        /// </summary>
        /// <param name="storyId">The story id.</param>
        /// <returns><c>true</c> if the story was added because it is new; <c>false</c> if the story already existed.</returns>
        public async Task<bool> AddIfMissing(string storyId)
        {
            var mongoClient = new MongoClient(connectionString);
            var db = mongoClient.GetDatabase("ListIngester");
            var collection = db.GetCollection<BsonDocument>("Stories");
            var dictionary = new Dictionary<string, string>
            {
                ["StoryId"] = storyId
            };
            var bsonDocument = new BsonDocument(dictionary);
            var result = await collection.FindAsync(bsonDocument);
            bool alreadyExists = await result.AnyAsync();
            if (!alreadyExists)
            {
                await collection.InsertOneAsync(bsonDocument);
            }

            return !alreadyExists;
        }
    }
}
