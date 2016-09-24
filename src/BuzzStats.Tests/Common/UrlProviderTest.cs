using NUnit.Framework;
using BuzzStats.Common;

namespace BuzzStats.Tests.Common
{
    [TestFixture]
    public class UrlProviderTest
    {
        [Test]
        public void TestStoryPage()
        {
            Assert.AreEqual("http://buzz.reality-tape.com/story.php?id=42", UrlProvider.StoryUrl(42));
        }
    }
}
