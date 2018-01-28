using Confluent.Kafka;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;

namespace BuzzStats.StoryUpdater.UnitTests
{
    [TestClass]
    public class OldestStoryUpdaterTest
    {
        [TestMethod]
        public void Update()
        {
            // arrange
            var repositoryMock = new Mock<IRepository>();
            var producerMock = new Mock<ISerializingProducer<Null, string>>();
            var oldestStoryUpdater = new OldestStoryUpdater(
                repositoryMock.Object,
                producerMock.Object,
                "outputTopic");

            repositoryMock.Setup(p => p.OldestCheckedStory())
                .ReturnsAsync(42);

            // act
            oldestStoryUpdater.Update();

            // assert
            producerMock.Verify(v => v.ProduceAsync("outputTopic", null, "42"), Times.Once());
            repositoryMock.Verify(v => v.UpdateLastCheckedDate(42));
        }

        [TestMethod]
        public void Update_WhenNoStoriesExist()
        {
            // arrange
            var repositoryMock = new Mock<IRepository>();
            var producerMock = new Mock<ISerializingProducer<Null, string>>();
            var oldestStoryUpdater = new OldestStoryUpdater(
                repositoryMock.Object,
                producerMock.Object,
                "outputTopic");

            repositoryMock.Setup(p => p.OldestCheckedStory())
                .ReturnsAsync((int?)null);

            // act
            oldestStoryUpdater.Update();

            // assert
            producerMock.Verify(v => v.ProduceAsync("outputTopic", null, It.IsAny<string>()), Times.Never());
            repositoryMock.Verify(v => v.UpdateLastCheckedDate(It.IsAny<int>()), Times.Never());
        }
    }
}
