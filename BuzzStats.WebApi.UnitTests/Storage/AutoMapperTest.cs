using System;
using AutoMapper;
using BuzzStats.WebApi.DTOs;
using BuzzStats.WebApi.Storage;
using BuzzStats.WebApi.Storage.Entities;
using NUnit.Framework;
using StructureMap;

namespace BuzzStats.WebApi.UnitTests.Storage
{
    [TestFixture]
    public class AutoMapperTest
    {
        private IMapper _mapper;

        [SetUp]
        public void ValidateAutoMapper()
        {
            IContainer container = new StructureMapContainerBuilder().Create();
            _mapper = container.GetInstance<IMapper>();
            _mapper.ConfigurationProvider.AssertConfigurationIsValid();
        }

        [Test]
        public void MapCommentEntityToCommentWithStory()
        {
            CommentEntity commentEntity = new CommentEntity
            {
                CommentId = 42,
                Story = new StoryEntity
                {
                    StoryId = 10
                }
            };

            CommentWithStory commentWithStory = _mapper.Map<CommentWithStory>(commentEntity);
            Assert.IsNotNull(commentWithStory);
            Assert.AreEqual(42, commentWithStory.CommentId);
            Assert.AreEqual(10, commentWithStory.StoryId);
        }

        [Test]
        public void MapCommentEntityToCommentWithStory_WithNullStory()
        {
            CommentEntity commentEntity = new CommentEntity
            {
                CommentId = 42
            };

            CommentWithStory commentWithStory = _mapper.Map<CommentWithStory>(commentEntity);
            Assert.IsNotNull(commentWithStory);
            Assert.AreEqual(42, commentWithStory.CommentId);
            Assert.AreEqual(0, commentWithStory.StoryId);
        }

        [Test]
        public void MapStoryToStoryEntity()
        {
            // arrange
            Story story = new Story
            {
                Category = 1,
                CreatedAt = new DateTime(2017, 7, 26),
                IsRemoved = true,
                StoryId = 42,
                Title = "title",
                Url = "url",
                Username = "username"
            };

            // act
            StoryEntity storyEntity = _mapper.Map<StoryEntity>(story);

            // assert
            Assert.AreEqual(1, storyEntity.Category);
            Assert.AreEqual(new DateTime(2017, 7, 26), storyEntity.CreatedAt);
            Assert.AreEqual(42, storyEntity.StoryId);
            Assert.AreEqual("title", storyEntity.Title);
            Assert.AreEqual("url", storyEntity.Url);
            Assert.AreEqual("username", storyEntity.Username);
        }
    }
}