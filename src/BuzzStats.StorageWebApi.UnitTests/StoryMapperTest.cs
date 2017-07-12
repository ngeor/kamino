using System;
using BuzzStats.StorageWebApi.DTOs;
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
    }
}