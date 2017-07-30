using System;
using System.Collections.Generic;
using AutoMapper;
using BuzzStats.WebApi.DTOs;
using BuzzStats.WebApi.Storage;
using BuzzStats.WebApi.UnitTests.TestHelpers;
using BuzzStats.WebApi.Web;
using Moq;
using NUnit.Framework;

namespace BuzzStats.WebApi.UnitTests.Web
{
    [TestFixture]
    public class RecentCommentsControllerTest
    {
        private Mock<IStorageClient> _mockStorageClient;
        private Mock<IMapper> _mockMapper;
        private RecentCommentsController _recentCommentsController;

        [SetUp]
        public void SetUp()
        {
            MockHelper.InjectMocks(this);
            _recentCommentsController = MockHelper.Create<RecentCommentsController>(this);
        }

        [Test]
        public void Get()
        {
            // arrange
            _mockStorageClient.Setup(s => s.GetRecentComments())
                .Returns(new List<CommentWithStory>
                {
                    new CommentWithStory
                    {
                        CommentId = 12,
                        StoryId = 1,
                        Title = "first story",
                        Username = "first user",
                        VotesUp = 5,
                        CreatedAt = new DateTime(2017, 7, 30)
                    },
                    new CommentWithStory
                    {
                        CommentId = 13,
                        StoryId = 1
                    },
                    new CommentWithStory
                    {
                        CommentId = 24,
                        StoryId = 2
                    },
                    new CommentWithStory
                    {
                        CommentId = 15,
                        StoryId = 1
                    }
                });

            _mockMapper.Setup(m => m.Map<RecentComment>(It.IsAny<CommentWithStory>()))
                .Returns<CommentWithStory>(c => new RecentComment
                {
                    CommentId = c.CommentId,
                    Username = "user " + c.CommentId
                });

            // act
            var storyWithRecentComments = _recentCommentsController.Get();

            // assert
            var expected = new[]
            {
                new StoryWithRecentComments
                {
                    StoryId = 1,
                    Title = "first story",
                    Comments = new[]
                    {
                        new RecentComment
                        {
                            CommentId = 12,
                            Username = "user 12"
                        },
                        new RecentComment
                        {
                            CommentId = 13,
                            Username = "user 13"
                        },
                        new RecentComment
                        {
                            CommentId = 15,
                            Username = "user 15"
                        }
                    }
                },
                new StoryWithRecentComments
                {
                    StoryId = 2,
                    Comments = new[]
                    {
                        new RecentComment
                        {
                            CommentId = 24,
                            Username = "user 24"
                        }
                    }
                }
            };

            CollectionAssert.AreEqual(expected, storyWithRecentComments);
        }
    }
}