using System.Threading.Tasks;
using BuzzStats.WebApi.Crawl;
using BuzzStats.WebApi.DTOs;
using BuzzStats.WebApi.Parsing;
using BuzzStats.WebApi.UnitTests.TestHelpers;
using Moq;
using NUnit.Framework;

namespace BuzzStats.WebApi.UnitTests.Crawl
{
    [TestFixture]
    public class ListingTaskTest
    {
#pragma warning disable 0649
        private Mock<IParserClient> _mockParserClient;
        private Mock<IStoryProcessTopic> _mockStoryProcessTopic;
        private ListingTask _listingTask;
#pragma warning restore 0649

        [SetUp]
        public void SetUp()
        {
            MockHelper.InjectMocks(this);
            _listingTask = MockHelper.Create<ListingTask>(this);
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