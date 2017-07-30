using System;
using System.Configuration;
using System.Data.Common;
using System.Linq;
using NUnit.Framework;
using NGSoftware.Common;
using NodaTime;

namespace BuzzStats.Data.TestsBase
{
    [TestFixture]
    [Category("Integration")]
    public abstract class StoryDataLayerTestBase
    {
        private DbConnection _connection;
        private IStoryDataLayer _storyDataLayer;

        [SetUp]
        public void SetUp()
        {
            var connectionString = ConfigurationManager.ConnectionStrings["mysql"];
            Assert.IsNotNull(connectionString, "mysql connection string missing from app.config");
            _connection = connectionString.CreateConnection();
            _connection.Open();
            _connection.PrepareDatabase("sample.sql");
            _storyDataLayer = CreateStoryDataLayer(_connection);
        }

        protected abstract IStoryDataLayer CreateStoryDataLayer(DbConnection dbConnection);

        [TearDown]
        public virtual void TearDown()
        {
            _connection.SafeDispose();
        }

        [Test]
        public void TestRead()
        {
            StoryData story = _storyDataLayer.Read(42);
            Assert.IsNotNull(story);
            Assert.AreEqual(42, story.StoryId);
            Assert.AreEqual("test story 1", story.Title);
            Assert.AreEqual("http://ngeor.net/test", story.Url);
            Assert.AreEqual("ngeor.net", story.Host);
            Assert.AreEqual(1, story.Category);
            Assert.AreEqual(1, story.VoteCount);
            Assert.AreEqual("ngeor", story.Username);
            Assert.AreEqual(new DateTime(2015, 6, 1), story.CreatedAt);
            Assert.AreEqual(new DateTime(2015, 6, 1), story.DetectedAt);
            Assert.AreEqual(new DateTime(2015, 6, 1, 01, 00, 00), story.LastCheckedAt);
            Assert.AreEqual(new DateTime(2015, 6, 1), story.LastModifiedAt);
            Assert.AreEqual(1, story.TotalUpdates);
            Assert.AreEqual(2, story.TotalChecks);
            Assert.AreEqual(null, story.LastCommentedAt);
            Assert.AreEqual(null, story.RemovedAt);
        }

        [Test]
        public void TestReadRemovedStory()
        {
            StoryData story = _storyDataLayer.Read(45);
            Assert.IsNotNull(story);
            Assert.AreEqual(45, story.StoryId);
            Assert.IsTrue(story.RemovedAt.HasValue);
            Assert.AreEqual(new DateTime(2015, 6, 6), story.RemovedAt.Value);
        }

        [Test]
        public void TestReadMissingStoryReturnsNull()
        {
            StoryData story = _storyDataLayer.Read(1);
            Assert.IsNull(story);
        }

        [Test]
        public void TestCreate()
        {
            StoryData inputStory = new StoryData
            {
                StoryId = 100,
                Url = "http://ngeor.net/story100",
                Title = "story 100",
                Host = "ngeor.net",
                Username = "nikolaos",
                TotalChecks = 1,
                TotalUpdates = 1,
                CreatedAt = new DateTime(2015, 1, 2),
                DetectedAt = new DateTime(2015, 6, 6),
                LastCheckedAt = new DateTime(2015, 6, 6),
                LastModifiedAt = new DateTime(2015, 6, 6),
                VoteCount = 1
            };

            StoryData result = _storyDataLayer.Create(inputStory);
            Assert.IsNotNull(result);
            Assert.AreEqual(inputStory, result);

            StoryData read = _storyDataLayer.Read(100);
            Assert.IsNotNull(read);
            Assert.AreEqual(inputStory, read);
        }

        #region Mandatory story fields (storyId, title, username)

        [Test]
        public void TestCannotCreateWithoutStoryId()
        {
            Assert.Throws<InvalidStoryIdException>(() => _storyDataLayer.Create(new StoryData
            {
                Title = "hello",
                Username = "ngeor"
            }));
        }

        [Test]
        public void TestCannotCreateWithoutTitle()
        {
            Assert.Throws<ArgumentException>(() => _storyDataLayer.Create(new StoryData
            {
                StoryId = 100,
                Username = "ngeor"
            }));
        }

        [Test]
        public void TestCannotCreateWithoutUsername()
        {
            Assert.Throws<ArgumentException>(() => _storyDataLayer.Create(new StoryData
            {
                StoryId = 100,
                Title = "hello"
            }));
        }

        [Test]
        public void TestCanCreateWithMandatoryFields()
        {
            var result = _storyDataLayer.Create(new StoryData
            {
                StoryId = 100,
                Title = "hello",
                Username = "ngeor"
            });
            Assert.IsNotNull(result);
        }

        [Test]
        public void TestCreateThrowsExceptionWithNullData()
        {
            Assert.Throws<ArgumentNullException>(
                () => _storyDataLayer.Create((StoryData) null));
        }

        [Test]
        public void TestCreateExistingStoryId()
        {
            bool caught = false;
            try
            {
                _storyDataLayer.Create(new StoryData(42, "hello", "nikolaos"));
            }
            catch
            {
                caught = true;
            }

            Assert.IsTrue(caught, "Expected an exception to be thrown");
        }

        [Test]
        public void TestCreateRemovedStory()
        {
            // the story we'll insert
            StoryData input = new StoryData
            {
                StoryId = 100,
                Title = "my site",
                Category = 1,
                CreatedAt = new DateTime(2013, 6, 25),
                DetectedAt = new DateTime(2013, 6, 26),
                Host = "ngeor.net",
                LastCheckedAt = new DateTime(2013, 6, 27),
                LastCommentedAt = new DateTime(2013, 6, 28),
                LastModifiedAt = new DateTime(2013, 6, 29),
                RemovedAt = new DateTime(2013, 6, 30),
                TotalChecks = 1,
                TotalUpdates = 2,
                Url = "http://ngeor.net/",
                Username = "nikolaos",
                VoteCount = 2
            };

            // make sure we got something back and it has a positive database id
            StoryData result = _storyDataLayer.Create(input);
            Assert.IsNotNull(result);

            // compare it with the input. Temporarily set Id to zero to ignore it during comparison
            Assert.AreEqual(input, result);

            // dig into database to see it is really there.
            Assert.AreEqual(1, _connection.ExecuteScalar("SELECT COUNT(*) FROM Story WHERE StoryId=100"));

            // test also the LoadStory, it should return the same data back
            StoryData loadedStory = _storyDataLayer.Read(100);
            Assert.AreEqual(result, loadedStory);
        }

        #endregion

        [Test]
        public void TestUpdateThrowsExceptionWithNullData()
        {
            Assert.Throws<ArgumentNullException>(() => _storyDataLayer.Update((StoryData) null));
        }

        [Test]
        public void TestUpdateMissingStoryThrowsException()
        {
            Assert.Throws<InvalidStoryIdException>(() => _storyDataLayer.Update(new StoryData
            {
                StoryId = 90,
                Title = "title",
                Username = "ngeor"
            }));
        }

        [Test]
        public void TestUpdateWithZeroStoryId()
        {
            Assert.Throws<InvalidStoryIdException>(() => _storyDataLayer.Update(new StoryData(storyId: 0)));
        }

        [Test]
        public void TestUpdate()
        {
            StoryData inputStory = new StoryData
            {
                StoryId = 42,
                Url = "http://ngeor.net/story42",
                Title = "story 42",
                Host = "ngeor.net",
                Username = "nikolaos",
                TotalChecks = 2,
                TotalUpdates = 1,
                CreatedAt = new DateTime(2015, 1, 2),
                DetectedAt = new DateTime(2015, 6, 6),
                LastCheckedAt = new DateTime(2015, 6, 6),
                LastModifiedAt = new DateTime(2015, 6, 6),
                VoteCount = 1
            };
            _storyDataLayer.Update(inputStory);

            StoryData read = _storyDataLayer.Read(42);
            Assert.IsNotNull(read);
            Assert.AreEqual(inputStory, read);
        }

        [Test]
        public void TestQuery()
        {
            //Assert.Inconclusive();
        }

        [Test]
        public void TestOldestStoryDate()
        {
            DateTime oldestStoryDate = _storyDataLayer.OldestStoryDate();
            Assert.AreEqual(new DateTime(2015, 6, 1), oldestStoryDate);
        }

        [Test]
        public void TestGetStoryCountsPerHost()
        {
            var storyCountsPerHost = _storyDataLayer.GetStoryCountsPerHost();
            Assert.IsNotNull(storyCountsPerHost);
            Assert.AreEqual(2, storyCountsPerHost["ngeor.net"]);
            Assert.AreEqual(1, storyCountsPerHost["ngeor.org"]);
        }

        [Test]
        public void TestGetStoryCountsPerHostOnDateRange()
        {
            var storyCountsPerHost = _storyDataLayer.GetStoryCountsPerHost(
                new DateInterval(LocalDate.MinIsoValue, new LocalDate(2015, 6, 6)));
            Assert.IsNotNull(storyCountsPerHost);
            Assert.AreEqual(1, storyCountsPerHost["ngeor.net"]);
            Assert.AreEqual(1, storyCountsPerHost.Count);
        }

        [Test]
        public void TestGetStoryCountsPerUser()
        {
            var storyCountsPerUser = _storyDataLayer.GetStoryCountsPerUser();
            Assert.IsNotNull(storyCountsPerUser);
            Assert.AreEqual(2, storyCountsPerUser["ngeor"]);
            Assert.AreEqual(1, storyCountsPerUser["nikolaos"]);
        }

        [Test]
        public void TestGetStoryCountsPerUserOnDateRange()
        {
            var storyCountsPerUser = _storyDataLayer.GetStoryCountsPerUser(
                new DateInterval(new LocalDate(2015, 6, 6), LocalDate.MaxIsoValue));
            Assert.IsNotNull(storyCountsPerUser);
            Assert.AreEqual(1, storyCountsPerUser["ngeor"]);
            Assert.AreEqual(1, storyCountsPerUser["nikolaos"]);
        }

        [Test]
        public void TestGetMinMaxStats()
        {
            MinMaxStats minMaxStats = _storyDataLayer.GetMinMaxStats();
            Assert.IsNotNull(minMaxStats);
            Assert.AreEqual(new DateTime(2015, 6, 1, 01, 00, 00), minMaxStats.LastCheckedAt.Min);
            Assert.AreEqual(new DateTime(2015, 6, 3, 09, 00, 00), minMaxStats.LastCheckedAt.Max);
            Assert.AreEqual(1, minMaxStats.TotalChecks.Min);
            Assert.AreEqual(2, minMaxStats.TotalChecks.Max);
        }

        [Test]
        public void TestQueryCount()
        {
            var query = _storyDataLayer.Query();
            Assert.AreEqual(3, query.Count());
        }

        [Test]
        public void TestQueryCountWithExcludeIdsSingle()
        {
            var query = _storyDataLayer.Query();
            query = query.ExcludeIds(new[] {42});
            Assert.AreEqual(2, query.Count());
        }

        [Test]
        public void TestQueryCountWithExcludeIdsMulti()
        {
            var query = _storyDataLayer.Query();
            query = query.ExcludeIds(new[] {42, 43});
            Assert.AreEqual(1, query.Count());
        }

        [Test]
        public void TestQueryOrderByLastModifiedAtDesc()
        {
            var query = _storyDataLayer.Query();
            var ids = query.OrderBy(StorySortField.LastModifiedAt.Desc()).AsEnumerableOfIds();
            var id = ids.First();
            Assert.AreEqual(44, id);
        }

        [Test]
        public void TestQueryOrderByLastCheckedAtDesc()
        {
            var query = _storyDataLayer.Query();
            var ids = query.OrderBy(StorySortField.LastCheckedAt.Asc()).AsEnumerableOfIds();
            var id = ids.First();
            Assert.AreEqual(42, id);
        }

        [Test]
        public void TestQueryOrderByModificationAgeAsc()
        {
            var query = _storyDataLayer.Query();
            var ids = query.OrderBy(StorySortField.ModificationAge.Asc()).AsEnumerableOfIds();
            var id = ids.First();
            Assert.AreEqual(43, id);
        }
    }
}