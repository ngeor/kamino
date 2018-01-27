using BuzzStats.DTOs;
using BuzzStats.Parsing;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;

namespace BuzzStats.StoryIngester.UnitTests
{
    [TestClass]
    public class ProgramTest
    {
        [TestMethod]
        public void ConvertMessage()
        {
            // arrange
            // setup IParserClient
            var parseClientMock = new Mock<IParserClient>();
            var story = new Story
            {
                StoryId = 42
            };
            parseClientMock.Setup(p => p.Story(42))
                .ReturnsAsync(story);

            var app = new Program(parseClientMock.Object);
            var msg = "42";

            // act
            var result = app.Convert(msg).Result;

            // assert
            Assert.AreEqual(
                story,
                result);
        }
    }
}
