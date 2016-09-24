using NUnit.Framework;
using BuzzStats.Data.NHibernate.Entities;

namespace BuzzStats.Data.NHibernate.Tests
{
    [TestFixture]
    [Category("Entity")]
    public class WebPageEntityTest
    {
        [Test]
        public void TestMapEntityToData()
        {
            var entity = new WebPageEntity
            {
                Url = "url",
                Plugin = "data"
            };

            var expected = new WebPageData
            {
                Url = "url",
                Plugin = "data"
            };

            var actual = entity.ToData();
            Assert.AreEqual(expected.Url, actual.Url);
            Assert.AreEqual(expected.Plugin, actual.Plugin);
        }

        [Test]
        public void TestMapDataToEntity()
        {
            var data = new WebPageData
            {
                Url = "url",
                Plugin = "data"
            };

            var expected = new WebPageEntity
            {
                Url = "url",
                Plugin = "data"
            };

            var actual = data.ToEntity();
            Assert.AreEqual(expected.Url, actual.Url);
            Assert.AreEqual(expected.Plugin, actual.Plugin);
        }
    }
}
