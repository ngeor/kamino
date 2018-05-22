using System.Threading.Tasks;
using AutoMapper;
using BuzzStats.DTOs;
using MongoDB.Driver;
using Yak.Configuration;

namespace BuzzStats.ChangeTracker.Mongo
{
    public class Repository : IRepository
    {
        [ConfigurationValue]
        private string connectionString = "mongodb://127.0.0.1:27017";

        public Repository()
        {
            Mapper.Initialize(cfg =>
            {
                cfg.CreateMap<Story, MongoStory>();
                cfg.CreateMap<MongoStory, Story>();
            });
        }

        public async Task<Story> Load(int storyId)
        {
            IMongoCollection<MongoStory> collection = GetCollection();
            var mongoStory = await collection.Find(s => s.StoryId == storyId).SingleOrDefaultAsync();
            return Mapper.Map<DTOs.Story>(mongoStory);
        }

        private IMongoCollection<MongoStory> GetCollection()
        {
            var mongoClient = new MongoClient(connectionString);
            var db = mongoClient.GetDatabase("ChangeTracker");
            var collection = db.GetCollection<MongoStory>("Stories");
            return collection;
        }

        public async Task Save(Story story)
        {
            var mongoStory = Mapper.Map<MongoStory>(story);
            var collection = GetCollection();
            await collection.FindOneAndDeleteAsync(s => s.StoryId == story.StoryId);
            await collection.InsertOneAsync(mongoStory);
        }
    }
}
