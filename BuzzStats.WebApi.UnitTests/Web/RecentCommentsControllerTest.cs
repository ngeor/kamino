using System.Collections.Generic;
using BuzzStats.WebApi.DTOs;
using BuzzStats.WebApi.Storage;
using BuzzStats.WebApi.Web;
using Moq;
using NUnit.Framework;

namespace BuzzStats.WebApi.UnitTests.Web
{
    [TestFixture]
    public class RecentCommentsControllerTest
    {
        private RecentCommentsController _recentCommentsController;
        private Mock<IStorageClient> _mockStorageClient;

        [SetUp]
        public void SetUp()
        {
            _mockStorageClient = new Mock<IStorageClient>();
            _recentCommentsController = new RecentCommentsController(_mockStorageClient.Object);
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
                        VotesUp = 5
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
                            Username = "first user",
                            VotesUp = 5
                        },
                        new RecentComment
                        {
                            CommentId = 13
                        },
                        new RecentComment
                        {
                            CommentId = 15
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
                            CommentId = 24
                        }
                    }
                }
            };
            
            CollectionAssert.AreEqual(expected, storyWithRecentComments);
        }
    }
}