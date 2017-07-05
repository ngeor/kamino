using System;
using Moq;
using NUnit.Framework;
using NGSoftware.Common;
using BuzzStats.ApiServices;
using BuzzStats.Common;
using BuzzStats.Data;

namespace BuzzStats.Tests.ApiServices
{
    [TestFixture]
    public partial class ApiServiceTest
    {
        protected Mock<IStoryDataLayer> MockStoryDataLayer { get; private set; }
        protected Mock<ICommentDataLayer> MockCommentDataLayer { get; private set; }
        protected Mock<IStoryVoteDataLayer> MockStoryVoteDataLayer { get; private set; }
        protected IDbSession DbSession { get; private set; }
        protected ApiService ApiService { get; private set; }

        [SetUp]
        public virtual void SetUp()
        {
            TestableDateTime.UtcNowStrategy = () => new DateTime(2014, 2, 13);
            MockStoryDataLayer = new Mock<IStoryDataLayer>(MockBehavior.Strict);
            MockCommentDataLayer = new Mock<ICommentDataLayer>(MockBehavior.Strict);
            MockStoryVoteDataLayer = new Mock<IStoryVoteDataLayer>(MockBehavior.Strict);
            DbSession = Mock.Of<IDbSession>(s => s.Stories == MockStoryDataLayer.Object
                                                 && s.Comments == MockCommentDataLayer.Object
                                                 && s.StoryVotes == MockStoryVoteDataLayer.Object);
            ApiService = new ApiService(DbSession);
        }

        [TearDown]
        public virtual void TearDown()
        {
            TestableDateTime.UtcNowStrategy = null;
        }

        [Test]
        public void TestToAgoString()
        {
            Assert.False(string.IsNullOrWhiteSpace(TimeSpan.Zero.ToAgoString()));
        }

        [Test]
        public void TestGetRecentPopular()
        {
            var queryParameters = new CommentDataQueryParameters
            {
                CreatedAt = DateRange.After(14.Days().Ago()),
                SortBy = new[]
                {
                    CommentSortField.VotesUp.Desc(), CommentSortField.CreatedAt.Desc()
                },
                Count = 10
            };

            MockCommentDataLayer.Setup(d => d.Query(queryParameters)).Returns(new[]
            {
                new CommentData
                {
                    CommentId = 42,
                    Username = "nikolaos",
                    VotesUp = 5,
                    CreatedAt = TestableDateTime.UtcNow - TimeSpan.FromMinutes(10),
                    Story = new StoryData
                    {
                        StoryId = 1,
                        Title = "hello"
                    }
                }
            });

            CollectionAssert.AreEqual(
                new[]
                {
                    new CommentSummary
                    {
                        Age = TimeSpan.FromMinutes(10),
                        CommentId = 42,
                        Story = new CommentSummary.ParentStory
                        {
                            StoryId = 1,
                            Title = "hello"
                        },
                        Username = "nikolaos",
                        VotesUp = 5
                    }
                },
                ApiService.GetRecentPopularComments());
        }
    }
}