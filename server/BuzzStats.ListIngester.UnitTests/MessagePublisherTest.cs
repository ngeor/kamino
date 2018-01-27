using Confluent.Kafka;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;

namespace BuzzStats.ListIngester.UnitTests
{
    [TestClass]
    public class MessagePublisherTest
    {
        [TestMethod]
        public void HandleMessage()
        {
            // arrange
            var messageConverterMock = new Mock<IMessageConverter>();
            var producerMock = new Mock<ISerializingProducer<Null, string>>();
            var messagePublisher = new MessagePublisher(
                messageConverterMock.Object,
                producerMock.Object,
                "outputTopic"
                );

            messageConverterMock.Setup(p => p.ConvertAsync("hello"))
                .ReturnsAsync(new[] { "42", "84" });

            // act
            messagePublisher.HandleMessage("hello");

            // assert
            producerMock.Verify(v => v.ProduceAsync("outputTopic", null, "42"));
            producerMock.Verify(v => v.ProduceAsync("outputTopic", null, "84"));
        }        
    }
}
