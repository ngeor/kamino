// --------------------------------------------------------------------------------
// <copyright file="DataLayerGetCommentedStoryCountsPerUserTest.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/06/27
// * Time: 8:58 πμ
// --------------------------------------------------------------------------------

using System;
using System.Collections.Generic;
using NUnit.Framework;
using NGSoftware.Common;
using BuzzStats.Data.NHibernate.Entities;

namespace BuzzStats.Data.NHibernate.Tests
{
    [TestFixture]
    [Category("Integration")]
    public class DataLayerGetCommentedStoryCountsPerUserTest : LayerTestBase
    {
        protected override void InitializeData()
        {
            StoryEntity storyA = SaveStory(42, "my story", "nikolaos", new DateTime(2013, 6, 27));
            CommentEntity commentA1 = SaveComment(storyA, 100, "nikolaos", new DateTime(2013, 6, 27));
            SaveComment(storyA, 200, "test2", new DateTime(2013, 6, 28));
            SaveComment(storyA, 110, "test2", new DateTime(2013, 6, 29), commentA1);

            StoryEntity storyB = SaveStory(45, "my other story", "nikolaos", new DateTime(2013, 5, 27));
            CommentEntity commentB1 = SaveComment(storyB, 500, "nikolaos", new DateTime(2013, 5, 27));
            SaveComment(storyB, 600, "test2", new DateTime(2013, 5, 28));
            SaveComment(storyB, 510, "test3", new DateTime(2013, 5, 29), commentB1);
        }

        [Test]
        public void TestWithDateRange()
        {
            Dictionary<string, int> stats =
                DbSession.Stories.GetCommentedStoryCountsPerUser(
                    DateRange.Create(new DateTime(2013, 6, 27), new DateTime(2013, 6, 29)));
            Assert.AreEqual(2, stats.Count);
            Assert.AreEqual(1, stats["nikolaos"]);
            Assert.AreEqual(1, stats["test2"]);
        }

        [Test]
        public void TestWithoutDateRange()
        {
            Dictionary<string, int> stats = DbSession.Stories.GetCommentedStoryCountsPerUser();
            Assert.AreEqual(3, stats.Count);
            Assert.AreEqual(2, stats["nikolaos"]);
            Assert.AreEqual(2, stats["test2"]);
            Assert.AreEqual(1, stats["test3"]);
        }
    }
}
