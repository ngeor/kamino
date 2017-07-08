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
    }
}