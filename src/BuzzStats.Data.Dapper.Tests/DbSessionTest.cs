using NUnit.Framework;

namespace BuzzStats.Data.Dapper.Tests
{
    [TestFixture]
    public class DbSessionTest
    {
        [Test]
        public void TestStories()
        {
            DbSession dbSession = new DbSession();
            var stories = dbSession.Stories;
            Assert.IsNotNull(stories);
        }
    }
}