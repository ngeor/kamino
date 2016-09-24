// --------------------------------------------------------------------------------
// <copyright file="DataLayerGetCommentVotesTest.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/06/25
// * Time: 9:20 πμ
// --------------------------------------------------------------------------------

using System;
using NUnit.Framework;
using BuzzStats.Data.NHibernate.Entities;

namespace BuzzStats.Data.NHibernate.Tests
{
    [TestFixture]
    [Category("Integration")]
    public class DataLayerGetCommentVotesTest : LayerTestBase
    {
        private CommentEntity _comment;

        protected override void InitializeData()
        {
            StoryEntity story = SaveStory(1, "my site", "nikolaos", DateTime.UtcNow);
            SaveStoryVote(story, "nikolaos", DateTime.UtcNow);
            _comment = SaveComment(story, 100, "ngeor", new DateTime(2013, 6, 25));
            SaveCommentVote(_comment, 2, 1, false, new DateTime(2013, 5, 1));
            SaveCommentVote(_comment, 4, 10, true, new DateTime(2013, 5, 6));
        }

        [Test]
        public void TestGetCommentVotes()
        {
            CommentVoteData[] commentVotes = DbSession.CommentVotes.Query(new CommentData(commentId: 100));
            CommentVoteData[] expectedCommentVotes = new[]
            {
                new CommentVoteData
                {
                    Comment = new CommentData(commentId: 100),
                    CreatedAt = new DateTime(2013, 5, 1),
                    IsBuried = false,
                    VotesDown = 1,
                    VotesUp = 2
                },
                new CommentVoteData
                {
                    Comment = new CommentData(commentId: 100),
                    CreatedAt = new DateTime(2013, 5, 6),
                    IsBuried = true,
                    VotesDown = 10,
                    VotesUp = 4
                }
            };

            CollectionAssert.AreEqual(expectedCommentVotes, commentVotes);
        }
    }
}
