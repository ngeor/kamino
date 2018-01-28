using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace BuzzStats.Parsing.UnitTests
{
    [TestClass]
    public class UrlProviderTest
    {
        private IUrlProvider _urlProvider;

        [TestInitialize]
        public void SetUp()
        {
            _urlProvider = new UrlProvider("http://test.com/");
        }

        [TestMethod]
        public void ListingUrl_Home()
        {
            var listingUrl = _urlProvider.ListingUrl(StoryListing.Home);
            Assert.AreEqual("http://test.com/", listingUrl);
        }

        [TestMethod]
        public void ListingUrl_Upcoming()
        {
            var listingUrl = _urlProvider.ListingUrl(StoryListing.Upcoming);
            Assert.AreEqual("http://test.com/upcoming.php", listingUrl);
        }

        [TestMethod]
        public void ListingUrl_EnglishUpcoming()
        {
            var listingUrl = _urlProvider.ListingUrl(StoryListing.EnglishUpcoming);
            Assert.AreEqual("http://test.com/enupc.php", listingUrl);
        }
        
        [TestMethod]
        public void ListingUrl_Tech()
        {
            var listingUrl = _urlProvider.ListingUrl(StoryListing.Tech);
            Assert.AreEqual("http://test.com/tech.php", listingUrl);
        }

        [TestMethod]
        [ExpectedException(typeof(ArgumentOutOfRangeException))]
        public void ListingUrl_UnsupportedValue()
        {
            StoryListing invalidValue = (StoryListing)(-1);
            _urlProvider.ListingUrl(invalidValue);
        }

        [TestMethod]
        public void ListingUrl_Home_SecondPage()
        {
            var listingUrl = _urlProvider.ListingUrl(StoryListing.Home, 2);
            Assert.AreEqual("http://test.com/?page=2", listingUrl);
        }

        [TestMethod]
        public void ListingUrl_AllEnumsAreHandled()
        {
            foreach (StoryListing storyListing in Enum.GetValues(typeof(StoryListing)))
            {
                _urlProvider.ListingUrl(storyListing);
            }
        }

        [TestMethod]
        public void StoryUrl()
        {
            var storyUrl = _urlProvider.StoryUrl(42);
            Assert.AreEqual("http://test.com/story.php?id=42", storyUrl);
        }
        
        [TestMethod]
        public void StoryUrl_WithComment()
        {
            var storyUrl = _urlProvider.StoryUrl(42, 100);
            Assert.AreEqual("http://test.com/story.php?id=42#wholecomment100", storyUrl);
        }
    }
}