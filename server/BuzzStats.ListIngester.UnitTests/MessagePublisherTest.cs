using FluentAssertions;
using Microsoft.Extensions.Logging.Abstractions;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;
using System.Threading.Tasks;

namespace BuzzStats.ListIngester.UnitTests
{
    [TestClass]
    public class MessagePublisherTest
    {
        private Mock<IMessageConverter> messageConverterMock;
        private Mock<IRepository> repositoryMock;
        private MessagePublisher messagePublisher;

        [TestInitialize]
        public void SetUp()
        {
            messageConverterMock = new Mock<IMessageConverter>();
            repositoryMock = new Mock<IRepository>();
            messagePublisher = new MessagePublisher(
                messageConverterMock.Object,
                repositoryMock.Object,
                NullLogger.Instance
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

            // act & assert
            messagePublisher.HandleMessage("hello").Should().Equal("42", "84");
        }

        [TestMethod]
        public void HandleMessage_ExistingStory()
        {
            // arrange
            repositoryMock.Setup(r => r.AddIfMissing(It.IsAny<string>()))
                .Returns<string>(s => Task.FromResult(s == "42")); // 42 is new

            messageConverterMock.Setup(p => p.ConvertAsync("hello"))
                .ReturnsAsync(new[] { "42", "84" });

            // act & assert
            messagePublisher.HandleMessage("hello").Should().Equal("42");
        }
    }
}
