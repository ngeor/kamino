using System;
using System.Collections.Generic;
using System.Data.Common;
using System.Linq;
using Dapper;
using NGSoftware.Common;

namespace BuzzStats.Data.Dapper
{
    public class StoryDataLayer : IStoryDataLayer
    {
        private readonly DbConnection _connection;

        public StoryDataLayer(DbConnection connection)
        {
            _connection = connection;
        }

        public StoryData Create(StoryData newStory)
        {
            newStory.ValidatePreCreate();
            _connection.Execute(
                "INSERT INTO Story (StoryId, Title, Url, Host, Category, VoteCount, Username, " +
                "CreatedAt, DetectedAt, LastCheckedAt, LastModifiedAt, TotalUpdates, " +
                "TotalChecks, LastCommentedAt, RemovedAt) VALUES " +
                "(@StoryId, @Title, @Url, @Host, @Category, @VoteCount, @Username, " +
                "@CreatedAt, @DetectedAt, @LastCheckedAt, @LastModifiedAt, @TotalUpdates, " +
                "@TotalChecks, @LastCommentedAt, @RemovedAt)", newStory);
            return newStory;
        }

        public StoryData Read(int storyBusinessId)
        {
            return _connection.Query<StoryData>(
                "SELECT * FROM Story WHERE StoryId=@StoryId",
                new {StoryId = storyBusinessId}).SingleOrDefault();
        }

        public void Update(StoryData existingStory)
        {
            existingStory.ValidatePreUpdate();
            int rowsUpdated = _connection.Execute(
                "UPDATE Story SET Title=@Title, Url=@Url, Host=@Host, Category=@Category, " +
                "VoteCount=@VoteCount, Username=@Username, CreatedAt=@CreatedAt, " +
                "DetectedAt=@DetectedAt, LastCheckedAt=@LastCheckedAt, " +
                "LastModifiedAt=@LastModifiedAt, TotalUpdates=@TotalUpdates, " +
                "TotalChecks=@TotalChecks, LastCommentedAt=@LastCommentedAt, " +
                "RemovedAt=@RemovedAt " +
                "WHERE StoryId=@StoryId", existingStory);

            if (rowsUpdated == 0)
            {
                throw new InvalidStoryIdException();
            }
        }

        public IStoryQuery Query()
        {
            return new StoryQuery(_connection);
        }

        public DateTime OldestStoryDate()
        {
            return _connection.Query<DateTime>(
                "SELECT CreatedAt FROM Story " +
                "WHERE RemovedAt IS NULL " +
                "ORDER BY CreatedAt").FirstOrDefault();
        }

        public Dictionary<string, int> GetStoryCountsPerHost(DateRange dateRange)
        {
            var param = new
            {
                MinCreatedAt = dateRange.GetStartDate(),
                MaxCreatedAt = dateRange.GetStopDate(new DateTime(9999, 12, 31))
            };

            var result = _connection.Query<dynamic>(
                "SELECT COUNT(StoryId) StoryCount, Host FROM Story " +
                "WHERE CreatedAt>=@MinCreatedAt AND CreatedAt<@MaxCreatedAt AND RemovedAt IS NULL " +
                "GROUP BY Host", param);
            return result.ToDictionary(k => (string) k.Host, v => (int) v.StoryCount);
        }

        public Dictionary<string, int> GetStoryCountsPerUser(DateRange dateRange)
        {
            var param = new
            {
                MinCreatedAt = dateRange.GetStartDate(),
                MaxCreatedAt = dateRange.GetStopDate(new DateTime(9999, 12, 31))
            };

            var result = _connection.Query<dynamic>(
                "SELECT COUNT(StoryId) StoryCount, Username FROM Story " +
                "WHERE CreatedAt>=@MinCreatedAt AND CreatedAt<@MaxCreatedAt AND RemovedAt IS NULL " +
                "GROUP BY Username", param);
            return result.ToDictionary(k => (string) k.Username, v => (int) v.StoryCount);
        }

        public Dictionary<string, int> GetCommentedStoryCountsPerUser(DateRange dateRange)
        {
            throw new NotImplementedException();
        }

        public MinMaxStats GetMinMaxStats()
        {
            var result = _connection.Query<dynamic>(
                "SELECT MIN(LastCheckedAt) MinLastCheckedAt, MAX(LastCheckedAt) MaxLastCheckedAt, " +
                "MIN(TotalChecks) MinTotalChecks, MAX(TotalChecks) MaxTotalChecks FROM Story " +
                "WHERE RemovedAt IS NULL").Single();

            // casting because it can be MySqlDateTime
            DateTime min = (DateTime) result.MinLastCheckedAt;
            DateTime max = (DateTime) result.MaxLastCheckedAt;

            return new MinMaxStats
            {
                LastCheckedAt = new MinMaxValue<DateTime>
                {
                    Min = min,
                    Max = max
                },
                TotalChecks = new MinMaxValue<int>
                {
                    Min = result.MinTotalChecks,
                    Max = result.MaxTotalChecks
                }
            };
        }
    }
}