using MongoDB.Driver;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace BuzzStats.Web.Mongo
{
    public class Repository : IRepository
    {
        private readonly string connectionString;

        public Repository(string connectionString)
        {
            this.connectionString = connectionString ?? throw new ArgumentNullException(nameof(connectionString));
        }

        private MongoClient CreateClient() => new MongoClient(connectionString);
        private IMongoDatabase GetDb() => CreateClient().GetDatabase("BuzzStatsWeb");

        public async Task<IEnumerable<RecentActivity>> GetRecentActivity()
        {
            var collection = GetDb().GetCollection<RecentActivity>("RecentActivity");
            var cursor = await collection.FindAsync(FilterDefinition<RecentActivity>.Empty);
            return await cursor.ToListAsync();
        }

        public async Task<IEnumerable<StoryWithRecentComments>> GetStoriesWithRecentComments()
        {
            var collection = GetDb().GetCollection<StoryWithRecentComments>("StoryWithRecentComments");
            var cursor = await collection.FindAsync(FilterDefinition<StoryWithRecentComments>.Empty);
            return await cursor.ToListAsync();
        }

        public async Task Save(RecentActivity recentActivity)
        {
            var collection = GetDb().GetCollection<RecentActivity>("RecentActivity");
            await collection.InsertOneAsync(recentActivity);
        }
    }
}
