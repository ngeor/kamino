using System;
using BuzzStats.DTOs;
using BuzzStats.Parsing.DTOs;
using BuzzStats.WebApi.DTOs;
using BuzzStats.WebApi.Storage;
using BuzzStats.WebApi.Storage.Entities;
using NUnit.Framework;

namespace BuzzStats.WebApi.UnitTests.Storage
{
    [TestFixture]
    public class StoryMapperTest
    {
        private StoryMapper _storyMapper;

        [SetUp]
        public void SetUp()
        {
            _storyMapper = new StoryMapper();
        }

        [Test]
        public void ToStoryVoteEntities_WithNullVotes_ShouldReturnEmptyArray()
        {
            // arrange
            var story = new Story
            {
                StoryId = 42
            };

            var storyEntity = new StoryEntity();

            // act
            var storyVoteEntities = _storyMapper.ToStoryVoteEntities(story, storyEntity);

            // assert
            Assert.AreEqual(0, storyVoteEntities.Length);
        }

        [Test]
        public void ToStoryVoteEntities_WithEmptyVotes_ShouldReturnEmptyArray()
        {
            // arrange
            var story = new Story
            {
                StoryId = 42,
                Voters = Array.Empty<string>()
            };

            var storyEntity = new StoryEntity();

            // act
            var storyVoteEntities = _storyMapper.ToStoryVoteEntities(story, storyEntity);

            // assert
            Assert.AreEqual(0, storyVoteEntities.Length);
        }

        [Test]
        public void ToStoryVoteEntities_WithOneVote_ShouldSetFields()
        {
            // arrange
            var story = new Story
            {
                StoryId = 42,
                Voters = new[] {"user"}
            };

            var storyEntity = new StoryEntity();

            // act
            var storyVoteEntities = _storyMapper.ToStoryVoteEntities(story, storyEntity);

            // assert
            Assert.AreEqual(1, storyVoteEntities.Length);
            Assert.AreEqual("user", storyVoteEntities[0].Username);
            Assert.AreEqual(storyEntity, storyVoteEntities[0].Story);
        }

        [Test]
        public void ToCommentEntity_WithNoChildComments_MapsAllFields()
        {
            // arrange
            var comment = new Comment
            {
                CommentId = 42,
                Comments = null,
                CreatedAt = new DateTime(2017, 7, 15),
                IsBuried = false,
                Username = "user",
                VotesDown = 1,
                VotesUp = 2
            };
            
            var parentCommentEntity = new CommentEntity();

            var storyEntity = new StoryEntity();

            // act
            var firstComment = _storyMapper.ToCommentEntity(comment, parentCommentEntity, storyEntity);

            // assert
            Assert.AreEqual(42, firstComment.CommentId);
            Assert.AreEqual(parentCommentEntity, firstComment.ParentComment);
            Assert.AreEqual(new DateTime(2017, 7, 15), firstComment.CreatedAt);
            Assert.AreEqual(false, firstComment.IsBuried);
            Assert.AreEqual("user", firstComment.Username);
            Assert.AreEqual(1, firstComment.VotesDown);
            Assert.AreEqual(2, firstComment.VotesUp);
            Assert.AreEqual(storyEntity, firstComment.Story);
        }

        [Test]
        public void ToCommentEntity_MapsBuriedComments()
        {
            // arrange
            var comment = new Comment
            {
                IsBuried = true,
            };

            var storyEntity = new StoryEntity();

            // act
            var firstComment = _storyMapper.ToCommentEntity(comment, null, storyEntity);

            // assert
            Assert.AreEqual(true, firstComment.IsBuried);
        }
    }
}