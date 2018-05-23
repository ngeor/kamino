using System;
using BuzzStats.Parsing;
using FluentAssertions;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;

namespace BuzzStats.ListIngester.UnitTests
{
    [TestClass]
    public class MessageConverterTest
    {
        private MessageConverter converter;

        [TestInitialize]
        public void SetUp()
        {
            converter= new MessageConverter();
        }

        [TestMethod]
        public void OnDummyMessage_ItUpdatesTheHomePage()
        {
            // arrange
            var msg = "ping";

            // act
            var result = converter.Parse(msg);

            // assert
            result.Should().Be(Tuple.Create(StoryListing.Home, 0));
        }

        [TestMethod]
        public void OnUpcomingMessage_ItUpdatesTheUpcomingPage()
        {
            // arrange
            var msg = "Upcoming";

            // act
            var result = converter.Parse(msg);

            // assert
            result.Should().Be(Tuple.Create(StoryListing.Upcoming, 0));
        }

        [TestMethod]
        public void OnTechMessageWithPage_ItUpdatesTheCorrectPage()
        {
            // arrange
            var msg = "Tech 1";

            // act
            var result = converter.Parse(msg);

            // assert
            result.Should().Be(Tuple.Create(StoryListing.Tech, 1));
        }
    }
}
