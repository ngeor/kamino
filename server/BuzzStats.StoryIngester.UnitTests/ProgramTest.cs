using BuzzStats.DTOs;
using BuzzStats.Kafka;
using BuzzStats.Parsing;
using Confluent.Kafka;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;

namespace BuzzStats.StoryIngester.UnitTests
{
    [TestClass]
    public class ProgramTest
    {
        private Mock<IParserClient> parserClientMock;
        private Mock<ISerializingProducer<Null, Story>> producerMock;
        private Mock<IConsumerApp<Null, string>> consumerMock;

        private Program program;

        [TestInitialize]
        public void SetUp()
        {
            parserClientMock = new Mock<IParserClient>();
            producerMock = new Mock<ISerializingProducer<Null, Story>>();
            consumerMock = new Mock<IConsumerApp<Null, string>>();
            program = new Program(
                parserClientMock.Object,
                consumerMock.Object,
                producerMock.Object);
        }

        [TestMethod]
        public void OnConsumingStoryExpired_ProducesStoryParsed()
        {
            // arrange
            var story = new Story
            {
                StoryId = 42
            };

            consumerMock.Setup(p => p.Poll("StoryExpired"))
                .Raises(p => p.MessageReceived += null, consumerMock.Object, new Message<Null, string>("StoryExpired", 0, 0, null, "42", default(Timestamp), null));

            parserClientMock.Setup(p => p.StoryAsync(42))
                .ReturnsAsync(story);

            // act
            program.Poll();

            // assert
            producerMock.Verify(v => v.ProduceAsync("StoryParsed", null, story), Times.Once());
        }
    }
}
