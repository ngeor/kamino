using BuzzStats.Parsing;
using BuzzStats.Parsing.DTOs;
using Confluent.Kafka;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;
using System.Collections.Generic;
using System.Linq;

namespace BuzzStats.ListIngester.UnitTests
{
    [TestClass]
    public class ProgramTest
    {
        [TestMethod]
        public void SendMessage()
        {
            // arrange
            // setup IParserClient
            var parseClientMock = new Mock<IParserClient>();
            parseClientMock.Setup(p => p.Listing(StoryListing.Home, 0))
                .ReturnsAsync(new[]
                {
                    new StoryListingSummary
                    {
                        StoryId = 42
                    },
                    new StoryListingSummary
                    {
                        StoryId = 50
                    }
                });

            var app = new Program(parseClientMock.Object);
            var msg = "ping";

            // act
            var result = app.Convert(msg).Result;

            // assert
            CollectionAssert.AreEqual(new[]
            {
                "42",
                "50"
            },
            result.ToArray());
        }
    }
}
