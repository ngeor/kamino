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
        public void ToComments_WithNullComments_ReturnsEmptyArray()
        {
            // arrange
            var story = new Story
            {
                Comments = null
            };

            var storyEntity = new StoryEntity();

            // act
            var comments = _storyMapper.ToCommentEntities(story, storyEntity);

            // assert
            Assert.AreEqual(0, comments.Length);
        }

        [Test]
        public void ToComments_WithNoChildComments_MapsAllFields()
        {
            // arrange
            var story = new Story
            {
                Comments = new[]
                {
                    new Comment
                    {
                        CommentId = 42,
                        Comments = null,
                        CreatedAt = new DateTime(2017, 7, 15),
                        IsBuried = false,
                        Username = "user",
                        VotesDown = 1,
                        VotesUp = 2
                    }
                }
            };

            var storyEntity = new StoryEntity();

            // act
            var comments = _storyMapper.ToCommentEntities(story, storyEntity);

            // assert
            Assert.AreEqual(1, comments.Length);
            CommentEntity firstComment = comments[0].Entity;
            Assert.AreEqual(42, firstComment.CommentId);
            Assert.AreEqual(null, firstComment.ParentComment);
            Assert.AreEqual(new DateTime(2017, 7, 15), firstComment.CreatedAt);
            Assert.AreEqual(false, firstComment.IsBuried);
            Assert.AreEqual("user", firstComment.Username);
            Assert.AreEqual(1, firstComment.VotesDown);
            Assert.AreEqual(2, firstComment.VotesUp);
            Assert.AreEqual(storyEntity, firstComment.Story);
        }

        [Test]
        public void ToComments_MapsBuriedComments()
        {
            // arrange
            var story = new Story
            {
                Comments = new[]
                {
                    new Comment
                    {
                        IsBuried = true
                    }
                }
            };

            var storyEntity = new StoryEntity();

            // act
            var comments = _storyMapper.ToCommentEntities(story, storyEntity);

            // assert
            CommentEntity firstComment = comments[0].Entity;
            Assert.AreEqual(true, firstComment.IsBuried);
        }

        [Test]
        public void ToComments_WithChildComments_MapsAllFields()
        {
            // arrange
            var story = new Story
            {
                Comments = new[]
                {
                    new Comment
                    {
                        CommentId = 42,
                        Comments = new[]
                        {
                            new Comment
                            {
                                CommentId = 100
                            }
                        },
                        CreatedAt = new DateTime(2017, 7, 15),
                        IsBuried = false,
                        Username = "user",
                        VotesDown = 1,
                        VotesUp = 2
                    }
                }
            };

            var storyEntity = new StoryEntity();

            // act
            var comments = _storyMapper.ToCommentEntities(story, storyEntity);

            // assert
            Assert.AreEqual(1, comments.Length);
            Assert.AreEqual(42, comments[0].Entity.CommentId);
            Assert.AreEqual(100, comments[0].Children[0].Entity.CommentId);
            Assert.AreEqual(null, comments[0].Entity.ParentComment);
            Assert.AreEqual(comments[0].Entity, comments[0].Children[0].Entity.ParentComment);
            Assert.AreEqual(storyEntity, comments[0].Children[0].Entity.Story);
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