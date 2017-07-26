using System;
using System.Linq;
using System.Net;
using BuzzStats.Common;
using BuzzStats.Crawl;
using BuzzStats.Data;
using BuzzStats.Downloader;
using BuzzStats.Parsing;
using BuzzStats.UnitTests.DSL;
using Moq;
using NUnit.Framework;

namespace BuzzStats.UnitTests.Crawl
{
    [TestFixture]
    public class ListingTest
    {
        [Test]
        public void ShouldBeEqualToInstancesOfTheSameUrl()
        {
            const string url = "http://buzz.reality-tape.com/";
            Assert.AreEqual(
                new Listing(Mock.Of<IDownloaderService>(), Mock.Of<IUrlProvider>(), url, Mock.Of<IDbContext>()),
                new Listing(Mock.Of<IDownloaderService>(), Mock.Of<IUrlProvider>(), url, Mock.Of<IDbContext>()));
        }

        [Test]
        public void ShouldBeNonEqualToInstancesOfDifferentUrl()
        {
            const string url1 = "http://buzz.reality-tape.com/";
            const string url2 = "http://buzz.reality-tape.co.uk/";
            Assert.AreNotEqual(
                new Listing(Mock.Of<IDownloaderService>(), Mock.Of<IUrlProvider>(), url1, Mock.Of<IDbContext>()),
                new Listing(Mock.Of<IDownloaderService>(), Mock.Of<IUrlProvider>(), url2, Mock.Of<IDbContext>()));
        }

        [Test]
        public void ShouldUseUrlAsSourceId()
        {
            const string url = "http://buzz.reality-tape.com/";
            Assert.AreEqual(url,
                new Listing(Mock.Of<IDownloaderService>(), Mock.Of<IUrlProvider>(), url, Mock.Of<IDbContext>())
                    .SourceId);
        }

        [Test]
        public void ShouldSelectStoriesThatDoNotExistInTheDatabase()
        {
            // arrange
            const string listingUrl = "http://buzz.reality-tape.com/";
            const string storyUrl = "http://buzz/42";

            IDownloaderService downloader = Mock.Of<IDownloaderService>()
                .SetupDownloadStories(listingUrl, new StoryListingSummary(42));

            IUrlProvider urlProvider = Mock.Of<IUrlProvider>()
                .SetupStoryUrl(42, storyUrl);

            StoryData nullStory = null;
            IStoryDataLayer storyDataLayer = Mock.Of<IStoryDataLayer>(s => s.Read(42) == nullStory);

            Listing homepage = new Listing(downloader, urlProvider, listingUrl, storyDataLayer.BindDbContext());

            // act
            var result = homepage.GetChildren().ToArray();

            // assert
            CollectionAssert.AreEqual(new[] {new StoryLeaf(storyUrl, 42, homepage)}, result);
        }

        [Test]
        public void ShouldSelectStoriesThatExistInTheDatabaseWithDifferentVoteCount()
        {
            // arrange
            const string listingUrl = "http://buzz.reality-tape.com/";
            const string storyUrl = "http://buzz/42";

            IDownloaderService downloader = Mock.Of<IDownloaderService>()
                .SetupDownloadStories(listingUrl, new StoryListingSummary(42, voteCount: 4));

            IUrlProvider urlProvider = Mock.Of<IUrlProvider>()
                .SetupStoryUrl(42, storyUrl);

            IStoryDataLayer storyDataLayer = Mock.Of<IStoryDataLayer>(
                s => s.Read(42) == new StoryData {StoryId = 42, VoteCount = 3});

            Listing homepage = new Listing(downloader, urlProvider, listingUrl, storyDataLayer.BindDbContext());

            // act
            var result = homepage.GetChildren().ToArray();

            // assert
            CollectionAssert.AreEqual(new[] {new StoryLeaf(storyUrl, 42, homepage)}, result);
        }

        [Test]
        public void ShouldNotSelectStoriesThatExistInTheDatabaseWithTheSameVoteCount()
        {
            // arrange
            const string listingUrl = "http://buzz.reality-tape.com/";
            const string storyUrl = "http://buzz/42";

            IDownloaderService downloader = Mock.Of<IDownloaderService>()
                .SetupDownloadStories(listingUrl, new StoryListingSummary(42, voteCount: 4));

            IUrlProvider urlProvider = Mock.Of<IUrlProvider>()
                .SetupStoryUrl(42, storyUrl);

            IStoryDataLayer storyDataLayer = Mock.Of<IStoryDataLayer>(
                s => s.Read(42) == new StoryData {StoryId = 42, VoteCount = 4});

            Listing homepage = new Listing(downloader, urlProvider, listingUrl, storyDataLayer.BindDbContext());

            // act
            var result = homepage.GetChildren().ToArray();

            // assert
            CollectionAssert.AreEqual(new StoryLeaf[0], result);
        }

        [Test]
        public void ShouldSelectStoriesThatExistInTheDatabaseWithIndeterminateVoteCount()
        {
            // arrange
            const string listingUrl = "http://buzz.reality-tape.com/";
            const string storyUrl = "http://buzz/42";

            IDownloaderService downloader = Mock.Of<IDownloaderService>()
                .SetupDownloadStories(listingUrl, new StoryListingSummary(42));

            IUrlProvider urlProvider = Mock.Of<IUrlProvider>()
                .SetupStoryUrl(42, storyUrl);

            IStoryDataLayer storyDataLayer = Mock.Of<IStoryDataLayer>(
                s => s.Read(42) == new StoryData {StoryId = 42, VoteCount = 3});

            Listing homepage = new Listing(downloader, urlProvider, listingUrl, storyDataLayer.BindDbContext());

            // act
            var result = homepage.GetChildren().ToArray();

            // assert
            CollectionAssert.AreEqual(new[] {new StoryLeaf(storyUrl, 42, homepage)}, result);
        }

        [Test]
        public void ShouldNotDieIfDownloaderThrowsWebException()
        {
            // arrange
            const string listingUrl = "http://buzz.reality-tape.com/";

            // downloader should crash
            IDownloaderService downloader = Mock.Of<IDownloaderService>()
                .SetupDownloadStories(listingUrl, new WebException());
            IUrlProvider urlProvider = Mock.Of<IUrlProvider>();
            IStoryDataLayer storyDataLayer = Mock.Of<IStoryDataLayer>();
            Listing homepage = new Listing(downloader, urlProvider, listingUrl, storyDataLayer.BindDbContext());

            // act
            var result = homepage.GetChildren().ToArray();

            // assert
            CollectionAssert.AreEqual(new StoryLeaf[0], result);
        }

        [Test]
        public void ShouldThrowIfDownloaderIsNull()
        {
            Assert.That(
                () =>
                {
                    Listing homepage = new Listing(null, Mock.Of<IUrlProvider>(), "http://test.com/",
                        Mock.Of<IDbContext>());
                },
                Throws.Exception
                    .TypeOf<ArgumentNullException>()
                    .With.Property("ParamName")
                    .EqualTo("downloader"));
        }
    }
}