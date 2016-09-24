// --------------------------------------------------------------------------------
// <copyright file="StoryVoteDataLayerTest.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/06/03
// * Time: 8:17 πμ
// --------------------------------------------------------------------------------

using System;
using NUnit.Framework;
using BuzzStats.Data.NHibernate.Entities;

namespace BuzzStats.Data.NHibernate.Tests
{
    [TestFixture]
    [Category("Integration")]
    public class StoryVoteDataLayerTest : LayerTestBase
    {
        private StoryData _expectedStory1;
        private StoryData _expectedStory3;

        protected override void InitializeData()
        {
            // first insert it
            StoryEntity storyEntity1 = SaveStory(100, "hello", "nikolaos", new DateTime(2012, 1, 1), voteCount: 3);

            SaveStoryVote(storyEntity1, "nikolaos", new DateTime(2012, 12, 7));
            SaveStoryVote(storyEntity1, "nikolaos 2", new DateTime(2012, 12, 8));
            SaveStoryVote(storyEntity1, "nikolaos 3", new DateTime(2012, 12, 9));

            // one more story
            StoryEntity storyEntity2 = SaveStory(200, "second story", "another", new DateTime(2012, 2, 1), voteCount: 1);

            // cast one vote only
            SaveStoryVote(storyEntity2, "another", new DateTime(2012, 12, 10));

            // third story
            StoryEntity storyEntity3 = SaveStory(300, "third story", "final", new DateTime(2012, 3, 1), voteCount: 2);

            SaveStoryVote(storyEntity3, "final", new DateTime(2012, 12, 10));
            SaveStoryVote(storyEntity3, "final 2", new DateTime(2012, 12, 11));

            _expectedStory1 = new StoryData(storyId: 100);
            _expectedStory3 = new StoryData(storyId: 300);
        }

        [Test]
        public void TestExists()
        {
            Assert.IsTrue(DbSession.StoryVotes.Exists(_expectedStory1, "nikolaos"));
            Assert.IsTrue(DbSession.StoryVotes.Exists(_expectedStory1, "nikolaos 2"));
            Assert.IsTrue(DbSession.StoryVotes.Exists(_expectedStory1, "nikolaos 3"));
            Assert.IsFalse(DbSession.StoryVotes.Exists(_expectedStory1, "final"));
            Assert.IsFalse(DbSession.StoryVotes.Exists(_expectedStory1, "another"));
            Assert.IsFalse(DbSession.StoryVotes.Exists(_expectedStory1, "another one bites the dust"));
        }

        [Test]
        public void TestGetNewVotesFirstVotesAreExcluded()
        {
            StoryVoteData[] storyVotes = DbSession.StoryVotes.Query(10, null);
            Assert.IsNotNull(storyVotes);
            Assert.AreEqual(3, storyVotes.Length);

            StoryVoteData[] expected = new[]
            {
                new StoryVoteData
                {
                    Username = "final 2",
                    CreatedAt = new DateTime(2012, 12, 11),
                    Story = _expectedStory3
                },
                new StoryVoteData
                {
                    Username = "nikolaos 3",
                    CreatedAt = new DateTime(2012, 12, 9),
                    Story = _expectedStory1
                },
                new StoryVoteData
                {
                    Username = "nikolaos 2",
                    CreatedAt = new DateTime(2012, 12, 8),
                    Story = _expectedStory1
                }
            };

            // and compare
            CollectionAssert.AreEqual(expected, storyVotes);
        }
    }
}
