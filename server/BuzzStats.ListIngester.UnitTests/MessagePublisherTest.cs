using Confluent.Kafka;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;
using System.Threading.Tasks;

namespace BuzzStats.ListIngester.UnitTests
{
    [TestClass]
    public class MessagePublisherTest
    {
        private Mock<IMessageConverter> messageConverterMock;
        private Mock<ISerializingProducer<Null, string>> producerMock;
        private Mock<IRepository> repositoryMock;
        private MessagePublisher messagePublisher;

        [TestInitialize]
        public void SetUp()
        {
            messageConverterMock = new Mock<IMessageConverter>();
            producerMock = new Mock<ISerializingProducer<Null, string>>();
            repositoryMock = new Mock<IRepository>();
            messagePublisher = new MessagePublisher(
                messageConverterMock.Object,
                producerMock.Object,
                "outputTopic",
                repositoryMock.Object
            );
        }

        [TestMethod]
        public void HandleMessage_NewStories()
        {
            // arrange
            repositoryMock.Setup(r => r.AddIfMissing(It.IsAny<string>()))
                .ReturnsAsync(true); // all are new

            messageConverterMock.Setup(p => p.ConvertAsync("hello"))
                .ReturnsAsync(new[] { "42", "84" });

            // act
            messagePublisher.HandleMessage("hello");

            // assert
            producerMock.Verify(v => v.ProduceAsync("outputTopic", null, "42"), Times.Once());
            producerMock.Verify(v => v.ProduceAsync("outputTopic", null, "84"), Times.Once());
        }

        [TestMethod]
        public void HandleMessage_ExistingStory()
        {
            // arrange
            repositoryMock.Setup(r => r.AddIfMissing(It.IsAny<string>()))
                .Returns<string>(s => Task.FromResult(s == "42")); // 42 is new

            messageConverterMock.Setup(p => p.ConvertAsync("hello"))
                .ReturnsAsync(new[] { "42", "84" });

            // act
            messagePublisher.HandleMessage("hello");

            // assert
            producerMock.Verify(v => v.ProduceAsync("outputTopic", null, "42"), Times.Once());
            producerMock.Verify(v => v.ProduceAsync("outputTopic", null, "84"), Times.Never());
        }
    }
}
