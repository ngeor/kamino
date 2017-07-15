using System;
using NUnit.Framework;
using NGSoftware.Common;
using BuzzStats.Data.NHibernate.Entities;

namespace BuzzStats.Data.NHibernate.Tests
{
    [TestFixture]
    [Category("Entity")]
    public class StoryVoteEntityTest
    {
        [SetUp]
        public void SetUp()
        {
            TestableDateTime.UtcNowStrategy = () => new DateTime(2014, 2, 13);
        }

        [Test]
        public void TestMapEntityToData()
        {
            StoryVoteEntity instance = new StoryVoteEntity
            {
                Id = 42,
                Username = "ngeor",
                CreatedAt = TestableDateTime.UtcNow,
                Story = new StoryEntity {StoryId = 100, Title = "test story", Username = "bob"}
            };

            StoryVoteData expected = new StoryVoteData
            {
                Username = "ngeor",
                CreatedAt = TestableDateTime.UtcNow,
                Story = new StoryData {StoryId = 100}
            };

            StoryVoteData actual = instance.ToData();
            Assert.AreEqual(new StoryData {StoryId = 100}, actual.Story);
            Assert.AreEqual(expected.Username, actual.Username);
            Assert.AreEqual(expected.CreatedAt, actual.CreatedAt);
        }

        [Test]
        public void TestMapDataToEntity()
        {
            StoryVoteEntity expected = new StoryVoteEntity
            {
                Id = 0,
                Username = "ngeor",
                CreatedAt = TestableDateTime.UtcNow,
                Story = null
            };

            StoryVoteData instance = new StoryVoteData
            {
                Username = "ngeor",
                CreatedAt = TestableDateTime.UtcNow,
                Story = new StoryData()
            };

            StoryVoteEntity actual = instance.ToEntity();
            Assert.IsNull(actual.Story);
            Assert.AreEqual(expected.Username, actual.Username);
            Assert.AreEqual(expected.CreatedAt, actual.CreatedAt);
        }

        [Test]
        public void TestMapEnumerableOfEntityToData()
        {
            StoryVoteEntity[] instance = new StoryVoteEntity[]
            {
                new StoryVoteEntity
                {
                    Id = 42,
                    Username = "ngeor",
                    CreatedAt = TestableDateTime.UtcNow,
                    Story = new StoryEntity {StoryId = 100, Title = "test story", Username = "bob"}
                },
                new StoryVoteEntity
                {
                    Id = 42112,
                    Username = "anonymous",
                    CreatedAt = TestableDateTime.UtcNow.AddDays(1),
                    Story = new StoryEntity {StoryId = 200, Title = "test story 2", Username = "alice"}
                }
            };

            StoryVoteData[] expected = new StoryVoteData[]
            {
                new StoryVoteData
                {
                    Username = "ngeor",
                    CreatedAt = TestableDateTime.UtcNow,
                    Story = null
                },
                new StoryVoteData
                {
                    Username = "anonymous",
                    CreatedAt = TestableDateTime.UtcNow.AddDays(1),
                    Story = null
                }
            };

            StoryVoteData[] actual = instance.ToData();

            for (int i = 0; i < instance.Length; i++)
            {
                Assert.IsNotNull(actual[i].Story);
                Assert.AreEqual(expected[i].Username, actual[i].Username);
                Assert.AreEqual(expected[i].CreatedAt, actual[i].CreatedAt);
            }
        }
    }
}