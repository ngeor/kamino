using System.Threading.Tasks;
using BuzzStats.Parsing;
using BuzzStats.Parsing.DTOs;
using BuzzStats.WebApi.Crawl;
using BuzzStats.WebApi.DTOs;
using BuzzStats.WebApi.Storage;
using BuzzStats.WebApi.UnitTests.TestHelpers;
using Moq;
using NUnit.Framework;

namespace BuzzStats.WebApi.UnitTests.Crawl
{
    [TestFixture]
    public class StoryProcessTaskTest
    {
#pragma warning disable 0649
        private Mock<IParserClient> _mockParserClient;
        private Mock<IStorageClient> _mockStorageClient;
        private Mock<IAsyncQueue<StoryListingSummary>> _mockQueue;
        private StoryProcessTask _storyProcessTask;
#pragma warning restore 0649
        
        [SetUp]
        public void SetUp()
        {
            MockHelper.InjectMocks(this);
            _storyProcessTask = MockHelper.Create<StoryProcessTask>(this);
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