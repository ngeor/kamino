// --------------------------------------------------------------------------------
// <copyright file="DataLayerGetStoryIdsTest.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/06/25
// * Time: 2:06 μμ
// --------------------------------------------------------------------------------

using System;
using System.Linq;
using NUnit.Framework;
using NGSoftware.Common;

namespace BuzzStats.Data.NHibernate.Tests
{
    [TestFixture]
    [Category("Integration")]
    public class DataLayerGetStoryIdsTest : LayerTestBase
    {
        protected override void InitializeData()
        {
            // the following 12 stories are used for GetStoryIds
            SaveStory(10, "story 1", "nikolaos", new DateTime(2012, 1, 1));
            SaveStory(15, "story 2", "nikolaos", new DateTime(2012, 2, 1));
            SaveStory(20, "story 3", "nikolaos", new DateTime(2012, 3, 1));
            SaveStory(25, "story 4", "nikolaos", new DateTime(2012, 4, 1));
            SaveStory(30, "story 5", "nikolaos", new DateTime(2012, 5, 1));
            SaveStory(35, "story 6", "nikolaos", new DateTime(2012, 6, 1));
            SaveStory(40, "story 7", "nikolaos", new DateTime(2012, 7, 1));
            SaveStory(45, "story 8", "nikolaos", new DateTime(2012, 8, 1));
            SaveStory(50, "story 9", "nikolaos", new DateTime(2012, 9, 1));
            SaveStory(55, "story a", "nikolaos", new DateTime(2012, 10, 1),
                lastCheckedAt: TestableDateTime.UtcNow.Subtract(TimeSpan.FromMinutes(10)));
            SaveStory(60, "story b", "nikolaos", new DateTime(2012, 11, 1), removedAt: new DateTime(2012, 12, 30));
            SaveStory(65, "story c", "nikolaos", new DateTime(2012, 12, 1));
        }

        [Test]
        public void TestGetNewStories()
        {
            StoryData[] stories = DbSession.Stories.Query().Take(2).AsEnumerable().ToArray();
            Assert.AreEqual(2, stories.Length);
            Assert.AreEqual(65, stories[0].StoryId);
            Assert.AreEqual(55, stories[1].StoryId);
        }

        [Test]
        public void TestGetStoriesSortByLastModifiedAtDescending()
        {
            StoryData[] stories = DbSession.Stories.Query()
                .OrderBy(StorySortField.LastModifiedAt.Desc())
                .Take(5)
                .AsEnumerable()
                .ToArray();

            Assert.IsNotNull(stories);
            Assert.AreEqual(5, stories.Length);

            // TODO: More testing here
        }

        [Test]
        public void TestGetStoryCount()
        {
            Assert.AreEqual(11, DbSession.Stories.Query().Count());
            Assert.AreEqual(1, DbSession.Stories.Query()
                .CreatedAt.InRange(DateRange.Create(new DateTime(2012, 1, 1), new DateTime(2012, 2, 1)))
                .Count());
            Assert.AreEqual(2, DbSession.Stories.Query()
                .CreatedAt.InRange(DateRange.Create(new DateTime(2012, 1, 1), new DateTime(2012, 2, 2)))
                .Count());
        }

        [Test]
        public void TestGetStoryIds()
        {
            int[] result = DbSession.Stories.Query()
                .Take(5)
                .AsEnumerableOfIds()
                .ToArray();

            Assert.IsNotNull(result);
            CollectionAssert.AreEqual(
                new[] {65, 55, 50, 45, 40},
                result);
        }

        [Test]
        public void TestGetStoryIdsExcludeRecent_Included()
        {
            int[] result = DbSession.Stories.Query()
                .LastCheckedAt.Before(2.Minutes().Ago())
                .Take(4)
                .AsEnumerableOfIds()
                .ToArray();

            Assert.IsNotNull(result);
            CollectionAssert.AreEqual(
                new[] {55},
                result);
        }

        [Test]
        public void TestGetStoryIdsExcludeRecent_Excluded()
        {
            int[] result = DbSession.Stories.Query()
                .LastCheckedAt.Before(15.Minutes().Ago())
                .Take(4)
                .AsEnumerableOfIds()
                .ToArray();

            Assert.IsNotNull(result);
            CollectionAssert.AreEqual(
                new int[] { },
                result);
        }

        [Test]
        public void TestGetStoryIdsPagination()
        {
            int[] result = DbSession.Stories.Query()
                .Skip(5)
                .Take(5)
                .AsEnumerableOfIds()
                .ToArray();

            Assert.IsNotNull(result);
            CollectionAssert.AreEqual(
                new[] {35, 30, 25, 20, 15},
                result);
        }

        [Test]
        public void TestGetStoryIdsSortByCreatedAtAscending()
        {
            int[] result = DbSession.Stories.Query()
                .OrderBy(StorySortField.CreatedAt.Asc())
                .Take(5)
                .AsEnumerableOfIds()
                .ToArray();

            Assert.IsNotNull(result);
            CollectionAssert.AreEqual(
                new[] {10, 15, 20, 25, 30},
                result);
        }
    }
}