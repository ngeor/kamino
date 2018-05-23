using MongoDB.Bson;
using MongoDB.Driver;
using System.Collections.Generic;
using System.Threading.Tasks;
using Yak.Configuration;

namespace BuzzStats.ListIngester.Mongo
{
    public class Repository : IRepository
    {
        [ConfigurationValue]
        private string connectionString = "mongodb://127.0.0.1:27017";

        /// <summary>
        /// Adds the given story id to the Stories collection.
        /// </summary>
        /// <param name="storyId">The story id.</param>
        /// <returns><c>true</c> if the story was added because it is new; <c>false</c> if the story already existed.</returns>
        public async Task<bool> AddIfMissing(int storyId)
        {
            //return true;
            var mongoClient = new MongoClient(connectionString);
            var db = mongoClient.GetDatabase("ListIngester");
            var collection = db.GetCollection<BsonDocument>("Stories");
            var dictionary = new Dictionary<string, int>
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
