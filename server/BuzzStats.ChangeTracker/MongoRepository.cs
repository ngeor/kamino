using System.Collections.Generic;
using System.Text;
using System.Threading.Tasks;
using AutoMapper;
using BuzzStats.DTOs;
using MongoDB.Driver;

namespace BuzzStats.ChangeTracker
{
    public class MongoRepository : IRepository
    {
        public MongoRepository()
        {
            Mapper.Initialize(cfg =>
            {
                cfg.CreateMap<Story, MongoStory>();
                cfg.CreateMap<MongoStory, Story>();
            });
        }

        public async Task<Story> Load(int storyId)
        {
            var mongoClient = new MongoClient("mongodb://192.168.99.100:27017");
            var db = mongoClient.GetDatabase("ChangeTracker");
            var collection = db.GetCollection<MongoStory>("Stories");
            var mongoStory = await collection.Find(s => s.StoryId == storyId).SingleOrDefaultAsync();
            return Mapper.Map<Story>(mongoStory);
        }

        public async Task Save(Story story)
        {
            var mongoStory = Mapper.Map<MongoStory>(story);
            var mongoClient = new MongoClient("mongodb://192.168.99.100:27017");
            var db = mongoClient.GetDatabase("ChangeTracker");
            var collection = db.GetCollection<MongoStory>("Stories");
            await collection.FindOneAndDeleteAsync(s => s.StoryId == story.StoryId);
            await collection.InsertOneAsync(mongoStory);
        }
    }
}
