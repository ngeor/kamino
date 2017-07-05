// --------------------------------------------------------------------------------
// <copyright file="StoryHandlerTest.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/10/17
// * Time: 06:16:12
// --------------------------------------------------------------------------------

using Moq;
using NUnit.Framework;
using BuzzStats.Crawl;
using BuzzStats.Data;
using BuzzStats.Persister;
using BuzzStats.Tests.Utils;

namespace BuzzStats.Tests.Crawl
{
    [TestFixture]
    public class StoryHandlerTest
    {
        [Test]
        public void ShouldPersistStory()
        {
            // arrange
            StubMessageBus messageBus = new StubMessageBus();
            BuzzStats.Parsing.Story story = new BuzzStats.Parsing.Story();
            StoryData storyData = new StoryData();
            Mock<IPersister> mockPersister = new Mock<IPersister>(MockBehavior.Strict);
            mockPersister.Setup(p => p.Save(story)).Returns(new PersisterResult(storyData, UpdateResult.Created));
            StoryHandler storyHandler = new StoryHandler(messageBus, mockPersister.Object);

            // act
            StoryDownloadedMessage message = new StoryDownloadedMessage(story, Mock.Of<ILeafSource>());
            messageBus.Publish(message);

            // assert
            mockPersister.VerifyAll();
        }

        [Test]
        public void ShouldPublishStoryCheckedMessage()
        {
            // arrange
            StubMessageBus messageBus = new StubMessageBus();
            BuzzStats.Parsing.Story story = new BuzzStats.Parsing.Story();
            StoryData storyData = new StoryData
            {
                StoryId = 42
            };
            Mock<IPersister> mockPersister = new Mock<IPersister>(MockBehavior.Strict);
            mockPersister.Setup(p => p.Save(story)).Returns(new PersisterResult(storyData, UpdateResult.Created));
            StoryHandler storyHandler = new StoryHandler(messageBus, mockPersister.Object);
            ILeafSource leafSource = Mock.Of<ILeafSource>();
            // act
            StoryDownloadedMessage message = new StoryDownloadedMessage(story, leafSource);
            messageBus.Publish(message);

            // assert
            Assert.IsTrue(
                messageBus.Contains(new StoryCheckedMessage(storyData, leafSource, UpdateResult.Created)),
                "StoryCheckedMessage message not found");
        }
    }
}