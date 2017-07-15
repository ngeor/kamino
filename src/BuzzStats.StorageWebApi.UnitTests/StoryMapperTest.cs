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
            Assert.AreEqual(42, comments[0].CommentId);
            Assert.AreEqual(null, comments[0].ParentComment);
            Assert.AreEqual(new DateTime(2017, 7, 15), comments[0].CreatedAt);
            Assert.AreEqual(false, comments[0].IsBuried);
            Assert.AreEqual("user", comments[0].Username);
            Assert.AreEqual(1, comments[0].VotesDown);
            Assert.AreEqual(2, comments[0].VotesUp);
            Assert.AreEqual(storyEntity, comments[0].Story);
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
            Assert.AreEqual(true, comments[0].IsBuried);
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
                        Comments = new []
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
            Assert.AreEqual(2, comments.Length);
            Assert.AreEqual(42, comments[0].CommentId);
            Assert.AreEqual(100, comments[1].CommentId);
            Assert.AreEqual(null, comments[0].ParentComment);
            Assert.AreEqual(comments[0], comments[1].ParentComment);
            Assert.AreEqual(storyEntity, comments[1].Story);
        }
    }
}