using System;
using System.Threading.Tasks;
using AutoMapper;
using BuzzStats.DTOs;
using MongoDB.Driver;

namespace BuzzStats.ChangeTracker
{
    public class MongoRepository : IRepository
    {
        private readonly string connectionString;

        public MongoRepository(string connectionString)
        {
            Mapper.Initialize(cfg =>
            {
                cfg.CreateMap<Story, MongoStory>();
                cfg.CreateMap<MongoStory, Story>();
            });
            this.connectionString = connectionString ?? throw new ArgumentNullException(nameof(connectionString));
        }

        public async Task<Story> Load(int storyId)
        {
            IMongoCollection<MongoStory> collection = GetCollection();
            var mongoStory = await collection.Find(s => s.StoryId == storyId).SingleOrDefaultAsync();
            return Mapper.Map<Story>(mongoStory);
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
