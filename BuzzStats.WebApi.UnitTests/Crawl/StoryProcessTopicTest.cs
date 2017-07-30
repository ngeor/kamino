using BuzzStats.WebApi.Crawl;
using BuzzStats.WebApi.DTOs;
using BuzzStats.WebApi.UnitTests.TestHelpers;
using Moq;
using NUnit.Framework;

namespace BuzzStats.WebApi.UnitTests.Crawl
{
    [TestFixture]
    public class StoryProcessTopicTest
    {
#pragma warning disable 0649
        private Mock<IAsyncQueue<StoryListingSummary>> _mockQueue;
        private StoryProcessTopic _storyProcessTopic;
#pragma warning restore 0649
        
        [SetUp]
        public void SetUp()
        {
            MockHelper.InjectMocks(this);
            _storyProcessTopic = MockHelper.Create<StoryProcessTopic>(this);
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