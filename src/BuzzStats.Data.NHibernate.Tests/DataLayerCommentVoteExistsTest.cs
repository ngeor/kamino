// --------------------------------------------------------------------------------
// <copyright file="DataLayerCommentVoteExistsTest.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/06/26
// * Time: 12:00 μμ
// --------------------------------------------------------------------------------

using System;
using NUnit.Framework;
using BuzzStats.Data.NHibernate.Entities;

namespace BuzzStats.Data.NHibernate.Tests
{
    [TestFixture]
    [Category("Integration")]
    public class DataLayerCommentVoteExistsTest : LayerTestBase
    {
        private CommentData _comment;
        private CommentData _comment2;

        protected override void InitializeData()
        {
            StoryEntity storyEntity = SaveStory(100, "my story", "nikolaos", new DateTime(2013, 6, 26));
            CommentEntity commentEntity = SaveComment(storyEntity, 200, "nikolaos", new DateTime(2013, 6, 26));
            SaveComment(storyEntity, 300, "nikolaos", new DateTime(2013, 6, 26));
            SaveCommentVote(commentEntity, 2, 1, false, new DateTime(2013, 6, 26));

            _comment = new CommentData(commentId: 200);
            _comment2 = new CommentData(commentId: 300);
        }

        [Test]
        public void Test()
        {
            Assert.IsTrue(DbSession.CommentVotes.Exists(_comment, 2, 1, false));
            Assert.IsFalse(DbSession.CommentVotes.Exists(_comment2, 2, 1, false));
            Assert.IsFalse(DbSession.CommentVotes.Exists(_comment, 2, 1, true));
            Assert.IsFalse(DbSession.CommentVotes.Exists(_comment, 2, 2, false));
            Assert.IsFalse(DbSession.CommentVotes.Exists(_comment, 1, 2, false));
        }
    }
}
