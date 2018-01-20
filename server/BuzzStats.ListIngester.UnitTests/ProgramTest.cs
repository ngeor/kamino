using BuzzStats.Kafka;
using BuzzStats.Parsing;
using BuzzStats.Parsing.DTOs;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;
using System.Linq;
using System.Threading.Tasks;

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

            // act
            var result = app.Convert("ping").Result;

            // assert
            CollectionAssert.AreEqual(new[]
            {
                "Found story 42",
                "Found story 50"
            },
            result.ToArray());
        }
    }
}
