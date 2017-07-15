// --------------------------------------------------------------------------------
// <copyright file="DataLayerGetVotesTest.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/06/24
// * Time: 3:08 μμ
// --------------------------------------------------------------------------------

using System;
using NUnit.Framework;
using BuzzStats.Data.NHibernate.Entities;

namespace BuzzStats.Data.NHibernate.Tests
{
    [TestFixture]
    [Category("Integration")]
    public class DataLayerGetVotesTest : LayerTestBase
    {
        [Test]
        public void EmptyDb()
        {
            StoryVoteData[] result = DbSession.StoryVotes.Query(new StoryData
            {
                StoryId = 42
            });

            Assert.IsEmpty(result);
        }

        [Test]
        public void OtherStory()
        {
            StoryEntity story = SaveStory(142, "hello", "nikolaos", DateTime.UtcNow);
            SaveStoryVote(story, "nikolaos", DateTime.UtcNow);

            StoryVoteData[] result = DbSession.StoryVotes.Query(new StoryData
            {
                StoryId = 42
            });

            Assert.IsEmpty(result);
        }

        [Test]
        public void OneVote()
        {
            StoryEntity story = SaveStory(42, "hello", "nikolaos", DateTime.UtcNow);
            StoryVoteEntity storyVote = SaveStoryVote(story, "nikolaos", DateTime.UtcNow);

            StoryVoteData[] result = DbSession.StoryVotes.Query(new StoryData
            {
                StoryId = 42
            });

            Assert.IsNotNull(result);
            Assert.AreEqual(1, result.Length);
            Assert.AreEqual(storyVote.ToData(), result[0]);
        }
    }
}