using BuzzStats.Parsing;
using BuzzStats.Parsing.DTOs;
using FluentAssertions;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;

namespace BuzzStats.ListIngester.UnitTests
{
    [TestClass]
    public class MessageConverterTest
    {
        private Mock<IParserClient> parserClientMock;
        private MessageConverter converter;

        [TestInitialize]
        public void SetUp()
        {
            parserClientMock = new Mock<IParserClient>();
            converter= new MessageConverter(parserClientMock.Object);
        }

        [TestMethod]
        public void OnDummyMessage_ItUpdatesTheHomePage()
        {
            // arrange
            // setup IParserClient
            parserClientMock.Setup(p => p.ListingAsync(StoryListing.Home, 0))
                .ReturnsAsync(new StoryListingSummaries(new[]
                {
                    new StoryListingSummary
                    {
                        StoryId = 42
                    },
                    new StoryListingSummary
                    {
                        StoryId = 50
                    }
                }));

            var msg = "ping";

            // act
            var result = converter.ConvertAsync(msg).Result;

            // assert
            result.Should().Equal("42", "50");
        }

        [TestMethod]
        public void OnUpcomingMessage_ItUpdatesTheUpcomingPage()
        {
            // arrange
            parserClientMock.Setup(p => p.ListingAsync(StoryListing.Upcoming, 0))
                .ReturnsAsync(new StoryListingSummaries(new[]
                {
                    new StoryListingSummary
                    {
                        StoryId = 42
                    },
                    new StoryListingSummary
                    {
                        StoryId = 50
                    }
                }));

            var msg = "Upcoming";

            // act
            var result = converter.ConvertAsync(msg).Result;

            // assert
            result.Should().Equal("42", "50");
        }

        [TestMethod]
        public void OnTechMessageWithPage_ItUpdatesTheCorrectPage()
        {
            // arrange
            parserClientMock.Setup(p => p.ListingAsync(StoryListing.Tech, 1))
                .ReturnsAsync(new StoryListingSummaries(new[]
                {
                    new StoryListingSummary
                    {
                        StoryId = 42
                    },
                    new StoryListingSummary
                    {
                        StoryId = 13
                    }
                }));

            var msg = "Tech 1";

            // act
            var result = converter.ConvertAsync(msg).Result;

            // assert
            result.Should().Equal("42", "13");
        }
    }
}
