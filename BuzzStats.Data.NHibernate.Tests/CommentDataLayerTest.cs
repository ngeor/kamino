// --------------------------------------------------------------------------------
// <copyright file="CommentDataLayerTest.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/06/03
// * Time: 8:17 πμ
// --------------------------------------------------------------------------------

using System;
using NUnit.Framework;
using NGSoftware.Common;
using BuzzStats.Data.NHibernate.Entities;

namespace BuzzStats.Data.NHibernate.Tests
{
    [TestFixture]
    [Category("Integration")]
    public class CommentDataLayerTest : LayerTestBase
    {
        private StoryEntity _story;
        private CommentEntity _comment;

        protected override void InitializeData()
        {
            // first insert it
            _story = new StoryEntity
            {
                Title = "hello",
                Username = "nikolaos",
                StoryId = 100
            };
            Save(_story);

            _comment = new CommentEntity
            {
                CommentId = 200,
                Story = _story,
                Username = "nikolaos",
                CreatedAt = new DateTime(2013, 1, 15)
            };
            Save(_comment);
        }

        [Test]
        public void TestGetCommentCount()
        {
            int result = DbSession.Comments.Count();
            Assert.AreEqual(1, result);
        }

        [Test]
        public void TestGetCommentCountWithDateRange()
        {
            int result = DbSession.Comments.Count(DateRange.Before(new DateTime(2013, 1, 15)));
            Assert.AreEqual(0, result);
        }

        [Test]
        public void TestLoadComment()
        {
            CommentData actual = DbSession.Comments.Read(200);
            CommentData expected = new CommentData
            {
                CommentId = 200,
                Story = new StoryData(storyId: 100),
                Username = "nikolaos",
                CreatedAt = new DateTime(2013, 1, 15)
            };

            Assert.AreEqual(expected, actual);
        }

        [Test]
        public void TestLoadNonExistingComment()
        {
            Assert.IsNull(DbSession.Comments.Read(210));
        }

        [Test]
        public void TestUpdate()
        {
            CommentData comment = DbSession.Comments.Read(200);
            comment.VotesUp = 2;
            comment.VotesDown = 3;
            DbSession.Comments.Update(comment);

            FlushAndReopenSession();

            CommentData actual = DbSession.Comments.Read(200);
            CommentData expected = new CommentData
            {
                CommentId = 200,
                Story = new StoryData(storyId: 100),
                Username = "nikolaos",
                CreatedAt = new DateTime(2013, 1, 15),
                VotesUp = 2,
                VotesDown = 3
            };

            Assert.AreEqual(expected, actual);
        }

        [Test]
        public void TestGetMostRecent()
        {
            CommentData[] comments = DbSession.Comments.Query(new CommentDataQueryParameters {Count = 6});
            Assert.IsNotNull(comments);
            // TODO IMPROVE TEST
            Assert.AreEqual(1, comments.Length);
        }

        [Test]
        public void TestGetMostRecentOfStory()
        {
            CommentData[] comments = DbSession.Comments.Query(
                new CommentDataQueryParameters {Count = 6, StoryId = _story.StoryId});
            Assert.IsNotNull(comments);
            // TODO IMPROVE TEST
            Assert.AreEqual(1, comments.Length);
        }
    }
}