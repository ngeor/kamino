using BuzzStats.WebApi.Crawl;
using BuzzStats.WebApi.DTOs;
using Moq;
using NUnit.Framework;

namespace BuzzStats.WebApi.UnitTests.Crawl
{
    [TestFixture]
    public class StoryProcessTopicTest
    {
        private Mock<IAsyncQueue<StoryListingSummary>> _mockQueue;
        private StoryProcessTopic _storyProcessTopic;

        [SetUp]
        public void SetUp()
        {
            _mockQueue = new Mock<IAsyncQueue<StoryListingSummary>>();
            _storyProcessTopic = new StoryProcessTopic(_mockQueue.Object);
        }

        [Test]
        public void Post()
        {
            // arrange
            StoryListingSummary storyListingSummary = new StoryListingSummary
            {
                StoryId = 42
            };

            // act
            _storyProcessTopic.Post(storyListingSummary);

            // assert
            _mockQueue.Verify(q => q.Push(storyListingSummary));
        }
    }
}