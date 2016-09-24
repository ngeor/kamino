// --------------------------------------------------------------------------------
// <copyright file="DataLayerGetCommentsTest.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/06/25
// * Time: 8:53 πμ
// --------------------------------------------------------------------------------

using System;
using NUnit.Framework;
using NGSoftware.Common;
using BuzzStats.Data.NHibernate.Entities;

namespace BuzzStats.Data.NHibernate.Tests
{
    [TestFixture]
    [Category("Integration")]
    public class DataLayerGetCommentsTest : LayerTestBase
    {
        protected override void InitializeData()
        {
            StoryEntity story = SaveStory(42, "my site", "nikolaos", TestableDateTime.UtcNow);
            SaveStoryVote(story, "nikolaos", TestableDateTime.UtcNow);
            var parentComment = SaveComment(story, 100, "ngeor", new DateTime(2013, 6, 25));
            SaveComment(story, 200, "test2", new DateTime(2013, 6, 26), parentComment);
        }

        [Test]
        public void TestChildComments()
        {
            CommentData[] comments = DbSession.Comments.Query(
                new StoryData(storyId: 42),
                new CommentData(commentId: 100));

            CommentData[] expectedComments = new[]
            {
                new CommentData
                {
                    CommentId = 200,
                    CreatedAt = new DateTime(2013, 6, 26),
                    DetectedAt = TestableDateTime.UtcNow,
                    IsBuried = false,
                    ParentComment = new CommentData(commentId: 100),
                    Story = new StoryData(storyId: 42),
                    Username = "test2"
                }
            };

            CollectionAssert.AreEqual(expectedComments, comments);
        }

        [Test]
        public void TestTopLevelOnly()
        {
            CommentData[] comments = DbSession.Comments.Query(new StoryData(storyId: 42), null);
            CommentData[] expectedComments = new[]
            {
                new CommentData
                {
                    CommentId = 100,
                    CreatedAt = new DateTime(2013, 6, 25),
                    DetectedAt = TestableDateTime.UtcNow,
                    IsBuried = false,
                    Story = new StoryData(storyId: 42),
                    Username = "ngeor"
                }
            };

            CollectionAssert.AreEqual(expectedComments, comments);
        }
    }
}
