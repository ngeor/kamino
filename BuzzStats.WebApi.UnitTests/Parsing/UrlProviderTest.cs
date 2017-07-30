using System;
using BuzzStats.WebApi.Parsing;
using Moq;
using NGSoftware.Common.Configuration;
using NUnit.Framework;

namespace BuzzStats.WebApi.UnitTests.Parsing
{
    [TestFixture]
    public class UrlProviderTest
    {
        private IAppSettings _appSettings;
        private IUrlProvider _urlProvider;

        [SetUp]
        public void SetUp()
        {
            _appSettings = Mock.Of<IAppSettings>(x => x["BuzzServerUrl"] == "http://test.com/");
            _urlProvider = new UrlProvider(_appSettings);
        }

        [Test]
        public void ListingUrl_Home()
        {
            var listingUrl = _urlProvider.ListingUrl(StoryListing.Home);
            Assert.AreEqual("http://test.com/", listingUrl);
        }

        [Test]
        public void ListingUrl_Upcoming()
        {
            var listingUrl = _urlProvider.ListingUrl(StoryListing.Upcoming);
            Assert.AreEqual("http://test.com/upcoming.php", listingUrl);
        }

        [Test]
        public void ListingUrl_EnglishUpcoming()
        {
            var listingUrl = _urlProvider.ListingUrl(StoryListing.EnglishUpcoming);
            Assert.AreEqual("http://test.com/enupc.php", listingUrl);
        }
        
        [Test]
        public void ListingUrl_Tech()
        {
            var listingUrl = _urlProvider.ListingUrl(StoryListing.Tech);
            Assert.AreEqual("http://test.com/tech.php", listingUrl);
        }

        [Test]
        public void ListingUrl_Home_SecondPage()
        {
            var listingUrl = _urlProvider.ListingUrl(StoryListing.Home, 1);
            Assert.AreEqual("http://test.com/?page=2", listingUrl);
        }

        [Test]
        public void ListingUrl_AllEnumsAreHandled()
        {
            foreach (StoryListing storyListing in Enum.GetValues(typeof(StoryListing)))
            {
                _urlProvider.ListingUrl(storyListing);
            }
        }

        [Test]
        public void StoryUrl()
        {
            var storyUrl = _urlProvider.StoryUrl(42);
            Assert.AreEqual("http://test.com/story.php?id=42", storyUrl);
        }
        
        [Test]
        public void StoryUrl_WithComment()
        {
            var storyUrl = _urlProvider.StoryUrl(42, 100);
            Assert.AreEqual("http://test.com/story.php?id=42#wholecomment100", storyUrl);
        }
    }
}