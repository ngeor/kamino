using System.Threading.Tasks;
using BuzzStats.WebApi.Crawl;
using BuzzStats.WebApi.DTOs;
using BuzzStats.WebApi.Parsing;
using BuzzStats.WebApi.Storage;
using Moq;
using NUnit.Framework;

namespace BuzzStats.WebApi.UnitTests.Crawl
{
    [TestFixture]
    public class StoryProcessTaskTest
    {
        private Mock<IParserClient> _mockParserClient;
        private Mock<IStorageClient> _mockStorageClient;
        private Mock<IAsyncQueue<StoryListingSummary>> _mockQueue;
        private StoryProcessTask _storyProcessTask;

        [SetUp]
        public void SetUp()
        {
            _mockParserClient = new Mock<IParserClient>(MockBehavior.Strict);
            _mockStorageClient = new Mock<IStorageClient>();
            _mockQueue = new Mock<IAsyncQueue<StoryListingSummary>>();
            _storyProcessTask = new StoryProcessTask(_mockParserClient.Object, _mockStorageClient.Object, _mockQueue.Object);
        }

        [Test]
        public async Task RunOnce()
        {
            // arrange
            StoryListingSummary storyListingSummary = new StoryListingSummary
            {
                StoryId = 42
            };
            _mockQueue.Setup(q => q.Pop()).Returns(storyListingSummary);

            var story = new Story();

            _mockParserClient.Setup(p => p.Story(42))
                .ReturnsAsync(story);

            // act
            var result = await _storyProcessTask.RunOnce();

            // assert
            _mockStorageClient.Verify(s => s.Save(story));
            Assert.AreEqual(story, result);
        }
    }
}