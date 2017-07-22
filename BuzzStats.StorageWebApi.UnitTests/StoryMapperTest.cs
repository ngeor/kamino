using System;
using BuzzStats.StorageWebApi.DTOs;
using BuzzStats.StorageWebApi.Entities;
using NUnit.Framework;

namespace BuzzStats.StorageWebApi.UnitTests
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
        public void ToStoryEntity_ShouldSetStoryId()
        {
            // arrange
            var story = new Story
            {
                StoryId = 42
            };

            // act
            var storyEntity = _storyMapper.ToStoryEntity(story);

            // assert
            Assert.AreEqual(42, storyEntity.StoryId);
        }

        [Test]
        public void ToStoryEntity_ShouldSetTitle()
        {
            // arrange
            var story = new Story
            {
                Title = "hello"
            };

            // act
            var storyEntity = _storyMapper.ToStoryEntity(story);

            // assert
            Assert.AreEqual("hello", storyEntity.Title);
        }

        [Test]
        public void ToStoryEntity_ShouldSetUrl()
        {
            // arrange
            var story = new Story
            {
                Url = "http://localhost"
            };

            // act
            var storyEntity = _storyMapper.ToStoryEntity(story);

            // assert
            Assert.AreEqual("http://localhost", storyEntity.Url);
        }

        [Test]
        public void ToStoryEntity_ShouldSetUsername()
        {
            // arrange
            var story = new Story
            {
                Username = "test"
            };

            // act
            var storyEntity = _storyMapper.ToStoryEntity(story);

            // assert
            Assert.AreEqual("test", storyEntity.Username);
        }

        [Test]
        public void ToStoryEntity_ShouldSetCreatedAt()
        {
            // arrange
            var story = new Story
            {
                CreatedAt = new DateTime(2017, 7, 12)
            };

            // act
            var storyEntity = _storyMapper.ToStoryEntity(story);

            // assert
            Assert.AreEqual(story.CreatedAt, storyEntity.CreatedAt);
        }

        [Test]
        public void ToStoryEntity_ShouldSetCategory()
        {
            // arrange
            var story = new Story
            {
                Category = 1
            };

            // act
            var storyEntity = _storyMapper.ToStoryEntity(story);

            // assert
            Assert.AreEqual(1, storyEntity.Category);
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

        [Test]
        public void UpdateStoryEntity_ReturnsExistingObjectInstance()
        {
            // arrange
            var existing = new StoryEntity
            {
                Title = "abc"
            };

            var updated = new StoryEntity
            {
                Title = "def"
            };

            // act
            var result = _storyMapper.UpdateStoryEntity(existing, updated);

            // assert
            Assert.AreSame(result, existing);
        }

        [Test]
        public void UpdateStoryEntity_SetsTitle()
        {
            // arrange
            var existing = new StoryEntity
            {
                Title = "abc"
            };

            var updated = new StoryEntity
            {
                Title = "def"
            };

            // act
            var result = _storyMapper.UpdateStoryEntity(existing, updated);

            // assert
            Assert.AreEqual("def", result.Title);
        }
    }
}