using BuzzStats.DTOs;
using BuzzStats.Kafka;
using Confluent.Kafka;
using FluentAssertions;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;
using System;
using System.Linq;

namespace BuzzStats.ChangeTracker.UnitTests
{
    [TestClass]
    public class ProgramTest
    {
        private Mock<IChangeDetector> changeDetectorMock;
        private Mock<IConsumerApp<Null, Story>> consumerMock;
        private Mock<ISerializingProducer<Null, StoryEvent>> producerMock;
        private Program program;

        [TestInitialize]
        public void SetUp()
        {
            changeDetectorMock = new Mock<IChangeDetector>();
            consumerMock = new Mock<IConsumerApp<Null, Story>>();
            producerMock = new Mock<ISerializingProducer<Null, StoryEvent>>();
            program = new Program(changeDetectorMock.Object, consumerMock.Object, producerMock.Object);
        }

        [TestMethod]
        public void ConsumesStoryParsed_ProducesStoryChanged()
        {
            // arrange
            var msg = new Story
            {
                StoryId = 42,
                CreatedAt = new DateTime(2018, 1, 26)
            };

            consumerMock.Setup(p => p.Poll("StoryParsed"))
                .Raises(p => p.MessageReceived += null, null, new Message<Null, Story>(null, 0, 0, null, msg, default(Timestamp), null));

            var expectedMsg = new StoryEvent
            {
                StoryId = 42,
                CreatedAt = new DateTime(2018, 1, 26),
                EventType = StoryEventType.StoryCreated
            };

            changeDetectorMock.Setup(p => p.FindChangesAsync(msg))
                .ReturnsAsync(new[] { expectedMsg });

            // act
            program.Poll();

            // assert
            producerMock.Verify(p => p.ProduceAsync("StoryChanged", null, expectedMsg), Times.Once());
        }
    }
}