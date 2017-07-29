using System.Threading.Tasks;
using BuzzStats.WebApi.DTOs;
using Moq;
using NUnit.Framework;

namespace BuzzStats.WebApi.UnitTests
{
    [TestFixture]
    public class ListingTaskTest
    {
        private Mock<IParserClient> _mockParserClient;
        private Mock<IStorageClient> _mockStorageClient;
        private ListingTask _listingTask;

        [SetUp]
        public void SetUp()
        {
            _mockParserClient = new Mock<IParserClient>();
            _mockStorageClient = new Mock<IStorageClient>();
            _listingTask = new ListingTask(_mockParserClient.Object, _mockStorageClient.Object);
        }

        [Test]
        public async Task RunOnce_DownloadsHomeStoriesAndPersistsThem()
        {
            // arrange
            _mockParserClient.Setup(c => c.Home()).ReturnsAsync(new[]
            {
                new StoryListingSummary
                {
                    StoryId = 42,
                    VoteCount = 1
                }
            });

            _mockParserClient.Setup(c => c.Story(42)).ReturnsAsync(new Story
            {
                StoryId = 42,
                Title = "hello world"
            });

            // act
            await _listingTask.RunOnce();

            // assert
            _mockStorageClient.Verify(c => c.Save(It.Is<Story>(s => s.StoryId == 42)));
        }
    }
}