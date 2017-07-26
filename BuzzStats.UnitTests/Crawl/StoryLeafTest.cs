// --------------------------------------------------------------------------------
// <copyright file="StorySourceTest.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/10/17
// * Time: 06:06:23
// --------------------------------------------------------------------------------

using System.Net;
using BuzzStats.Crawl;
using BuzzStats.Downloader;
using BuzzStats.UnitTests.Utils;
using Moq;
using NUnit.Framework;

namespace BuzzStats.UnitTests.Crawl
{
    [TestFixture]
    public class StoryLeafTest
    {
        [Test]
        public void ShouldNotifyAboutStory()
        {
            // arrange dependencies
            const string url = "http://buzz.reality-tape.com/";
            const int storyId = 42;

            BuzzStats.Parsing.Story story = new BuzzStats.Parsing.Story();
            IDownloaderService downloader = Mock.Of<IDownloaderService>(p => p.DownloadStory(url, storyId) == story);
            StubMessageBus messageBus = new StubMessageBus();
            ILeafSource leafSource = Mock.Of<ILeafSource>();

            // arrange SUT
            StoryLeaf storyLeaf = new StoryLeaf(url, storyId, leafSource);

            // act
            storyLeaf.Update(downloader, messageBus);

            // assert
            Assert.That(messageBus.Contains(new StoryDownloadedMessage(story, leafSource)));
        }

        [Test]
        public void ShouldNotDieIfDownloaderThrowsWebException()
        {
            // arrange dependencies
            const string url = "http://buzz.reality-tape.com/";
            const int storyId = 42;

            IDownloaderService downloader = Mock.Of<IDownloaderService>();
            Mock.Get(downloader).Setup(p => p.DownloadStory(url, storyId)).Throws(new WebException());
            StubMessageBus messageBus = new StubMessageBus();
            ILeafSource leafSource = Mock.Of<ILeafSource>();

            // arrange SUT
            StoryLeaf storyLeaf = new StoryLeaf(url, storyId, leafSource);

            // act
            storyLeaf.Update(downloader, messageBus);

            // assert
            Assert.IsFalse(messageBus.ContainsAny<StoryDownloadedMessage>());
        }


        [Test]
        public void ShouldNotDieIfDownloaderReturnsNullStory()
        {
            // arrange dependencies
            const string url = "http://buzz.reality-tape.com/";
            const int storyId = 42;

            BuzzStats.Parsing.Story story = null;
            IDownloaderService downloader = Mock.Of<IDownloaderService>(p => p.DownloadStory(url, storyId) == story);
            StubMessageBus messageBus = new StubMessageBus();
            ILeafSource leafSource = Mock.Of<ILeafSource>();

            // arrange SUT
            StoryLeaf storyLeaf = new StoryLeaf(url, storyId, leafSource);

            // act
            storyLeaf.Update(downloader, messageBus);

            // assert
            Assert.IsFalse(messageBus.ContainsAny<StoryDownloadedMessage>());
        }

        [Test]
        public void ShouldBeEqualToInstancesOfTheSameUrlAndStoryIdAndLeafSource()
        {
            const string url = "http://buzz.reality-tape.com/story.php?id=123";
            const int storyId = 123;
            ILeafSource leafSource = Mock.Of<ILeafSource>();
            Assert.AreEqual(new StoryLeaf(url, storyId, leafSource), new StoryLeaf(url, storyId, leafSource));
        }

        [Test]
        public void ShouldBeNonEqualToInstancesOfDifferentUrl()
        {
            const string url1 = "http://buzz.reality-tape.com/";
            const string url2 = "http://buzz.reality-tape.co.uk/";
            const int storyId = 123;
            ILeafSource leafSource = Mock.Of<ILeafSource>();
            Assert.AreNotEqual(new StoryLeaf(url1, storyId, leafSource), new StoryLeaf(url2, storyId, leafSource));
        }

        [Test]
        public void ShouldBeNonEqualToInstancesOfDifferentStoryId()
        {
            const string url = "http://buzz.reality-tape.com/story.php?id=123";
            const int storyId1 = 123;
            const int storyId2 = 456;
            ILeafSource leafSource = Mock.Of<ILeafSource>();
            Assert.AreNotEqual(new StoryLeaf(url, storyId1, leafSource), new StoryLeaf(url, storyId2, leafSource));
        }

        [Test]
        public void ShouldBeEqualToInstancesOfDifferentLeafSource()
        {
            const string url = "http://buzz.reality-tape.com/story.php?id=123";
            const int storyId = 123;
            Assert.AreEqual(new StoryLeaf(url, storyId, Mock.Of<ILeafSource>()),
                new StoryLeaf(url, storyId, Mock.Of<ILeafSource>()));
        }
    }
}