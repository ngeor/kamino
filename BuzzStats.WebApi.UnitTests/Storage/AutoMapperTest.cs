using System;
using AutoMapper;
using BuzzStats.WebApi.DTOs;
using BuzzStats.WebApi.IoC;
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
            // arrange
            CommentEntity commentEntity = new CommentEntity
            {
                CommentId = 42,
                Username = "user",
                VotesUp = 1,
                CreatedAt = new DateTime(2017, 7, 30),
                Story = new StoryEntity
                {
                    StoryId = 10,
                    Title = "story title"
                }
            };

            // act
            CommentWithStory commentWithStory = _mapper.Map<CommentWithStory>(commentEntity);

            // assert
            Assert.IsNotNull(commentWithStory);
            Assert.AreEqual(42, commentWithStory.CommentId);
            Assert.AreEqual(10, commentWithStory.StoryId);
            Assert.AreEqual("user", commentWithStory.Username);
            Assert.AreEqual("story title", commentWithStory.Title);
            Assert.AreEqual(1, commentWithStory.VotesUp);
            Assert.AreEqual(new DateTime(2017, 7, 30), commentWithStory.CreatedAt);
        }

        [Test]
        public void MapCommentEntityToCommentWithStory_WithNullStory()
        {
            // arrange
            CommentEntity commentEntity = new CommentEntity
            {
                CommentId = 42
            };

            // act
            CommentWithStory commentWithStory = _mapper.Map<CommentWithStory>(commentEntity);

            // assert
            Assert.IsNotNull(commentWithStory);
            Assert.AreEqual(42, commentWithStory.CommentId);
            Assert.AreEqual(0, commentWithStory.StoryId);
        }

        [Test]
        public void MapCommentWithStoryToRecentComment()
        {
            // arrange
            CommentWithStory commentWithStory = new CommentWithStory
            {
                CommentId = 42,
                CreatedAt = new DateTime(2017, 7, 30),
                StoryId = 1,
                Title = "Story title",
                Username = "username",
                VotesUp = 1
            };

            // act
            RecentComment recentComment = _mapper.Map<RecentComment>(commentWithStory);

            // assert
            Assert.IsNotNull(recentComment);
            Assert.AreEqual(42, recentComment.CommentId);
            Assert.AreEqual(new DateTime(2017, 7, 30), recentComment.CreatedAt);
            Assert.AreEqual("username", recentComment.Username);
            Assert.AreEqual(1, recentComment.VotesUp);
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

        [Test]
        public void MapRecentActivityEntityToRecentActivity_OnlyStory()
        {
            // arrange
            RecentActivityEntity recentActivityEntity = new RecentActivityEntity
            {
                CreatedAt = new DateTime(2017, 7, 31),
                Story = new StoryEntity
                {
                    StoryId = 42,
                    Title = "my story",
                    Username = "username",
                    CreatedAt = new DateTime(2017, 7, 30)
                }
            };

            // act
            RecentActivity recentActivity = _mapper.Map<RecentActivity>(recentActivityEntity);

            // assert
            Assert.AreEqual(42, recentActivity.StoryId);
            Assert.AreEqual(0, recentActivity.CommentId);
            Assert.AreEqual(new DateTime(2017, 7, 31), recentActivity.CreatedAt);
            Assert.AreEqual("username", recentActivity.StoryUsername);
            Assert.AreEqual(null, recentActivity.StoryVoteUsername);
            Assert.AreEqual(null, recentActivity.CommentUsername);
            Assert.AreEqual(new DateTime(2017, 7, 30), recentActivity.StoryCreatedAt);
        }

        [Test]
        public void MapRecentActivityEntityToRecentActivity_WithStoryVote()
        {
            // arrange
            RecentActivityEntity recentActivityEntity = new RecentActivityEntity
            {
                CreatedAt = new DateTime(2017, 7, 31),
                Story = new StoryEntity
                {
                    StoryId = 42,
                    Title = "my story",
                    Username = "username"
                },
                StoryVote = new StoryVoteEntity
                {
                    Username = "voter"
                }
            };

            // act
            RecentActivity recentActivity = _mapper.Map<RecentActivity>(recentActivityEntity);

            // assert
            Assert.AreEqual(42, recentActivity.StoryId);
            Assert.AreEqual(0, recentActivity.CommentId);
            Assert.AreEqual(new DateTime(2017, 7, 31), recentActivity.CreatedAt);
            Assert.AreEqual("username", recentActivity.StoryUsername);
            Assert.AreEqual("voter", recentActivity.StoryVoteUsername);
            Assert.AreEqual(null, recentActivity.CommentUsername);
        }

        [Test]
        public void MapRecentActivityEntityToRecentActivity_WithComment()
        {
            // arrange
            RecentActivityEntity recentActivityEntity = new RecentActivityEntity
            {
                CreatedAt = new DateTime(2017, 7, 31),
                Story = new StoryEntity
                {
                    StoryId = 42,
                    Title = "my story",
                    Username = "username"
                },
                Comment = new CommentEntity
                {
                    Username = "commentor",
                    VotesUp = 2,
                    CreatedAt = new DateTime(2017, 7, 29)
                }
            };

            // act
            RecentActivity recentActivity = _mapper.Map<RecentActivity>(recentActivityEntity);

            // assert
            Assert.AreEqual(42, recentActivity.StoryId);
            Assert.AreEqual(0, recentActivity.CommentId);
            Assert.AreEqual(new DateTime(2017, 7, 31), recentActivity.CreatedAt);
            Assert.AreEqual("username", recentActivity.StoryUsername);
            Assert.AreEqual(null, recentActivity.StoryVoteUsername);
            Assert.AreEqual("commentor", recentActivity.CommentUsername);
            Assert.AreEqual(new DateTime(2017, 7, 29), recentActivity.CommentCreatedAt);
        }
    }
}
