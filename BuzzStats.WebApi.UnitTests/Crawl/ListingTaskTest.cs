using System.Threading.Tasks;
using BuzzStats.WebApi.Crawl;
using BuzzStats.WebApi.DTOs;
using BuzzStats.WebApi.Parsing;
using Moq;
using NUnit.Framework;

namespace BuzzStats.WebApi.UnitTests.Crawl
{
    [TestFixture]
    public class ListingTaskTest
    {
        private Mock<IParserClient> _mockParserClient;
        private Mock<IStoryProcessTopic> _mockStoryProcessTopic;
        private ListingTask _listingTask;

        [SetUp]
        public void SetUp()
        {
            _mockParserClient = new Mock<IParserClient>(MockBehavior.Strict);
            _mockStoryProcessTopic = new Mock<IStoryProcessTopic>(MockBehavior.Strict);
            _listingTask = new ListingTask(_mockParserClient.Object, _mockStoryProcessTopic.Object);
        }

        [Test]
        public async Task RunOnce_DownloadsHomeStoriesAndPersistsThem()
        {
            // arrange
            var storyListingSummary = new StoryListingSummary
            {
                StoryId = 42,
                VoteCount = 1
            };
            
            _mockParserClient.Setup(c => c.Listing(StoryListing.Home, 2)).ReturnsAsync(new[]
            {
                storyListingSummary
            });

            // act
            await _listingTask.RunOnce(StoryListing.Home, 2);

            // assert
            _mockStoryProcessTopic.Verify(c => c.Post(storyListingSummary));
        }
    }
}