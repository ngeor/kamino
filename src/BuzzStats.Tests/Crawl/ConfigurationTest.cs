// --------------------------------------------------------------------------------
// <copyright file="ConfigurationTest.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/10/17
// * Time: 08:37:41
// --------------------------------------------------------------------------------

using NUnit.Framework;
using BuzzStats.Crawl;

namespace BuzzStats.Tests.Crawl
{
    [TestFixture]
    public class ConfigurationTest
    {
        [Test]
        public void ShouldReadListingSourcesFromConfigFile()
        {
            IConfiguration c = new BuzzStats.Crawl.Configuration();
            CollectionAssert.AreEqual(new[]
            {
                "http://buzz.reality-tape.com/",
                "http://buzz.reality-tape.com/upcoming.php",
                "http://buzz.reality-tape.com/enupc.php",
                "http://buzz.reality-tape.com/tech.php"
            }, c.ListingSources);
        }

        [Test]
        public void ShouldNotSkipIngestersWhenSkipIngestersParameterIsNotPassed()
        {
            IConfiguration c = new BuzzStats.Crawl.Configuration();
            Assert.IsFalse(c.SkipIngesters);
        }

        [Test]
        public void ShouldSkipIngestersWhenSkipIngestersParameterIsPassed()
        {
            IConfiguration c = new BuzzStats.Crawl.Configuration("-skipIngesters");
            Assert.IsTrue(c.SkipIngesters);
        }

        [Test]
        public void ShouldNotSkipPollersWhenSkipPollersParameterIsNotPassed()
        {
            IConfiguration c = new BuzzStats.Crawl.Configuration();
            Assert.IsFalse(c.SkipPollers);
        }

        [Test]
        public void ShouldSkipPollersWhenSkipPollersParameterIsPassed()
        {
            IConfiguration c = new BuzzStats.Crawl.Configuration("-skipPollers");
            Assert.IsTrue(c.SkipPollers);
        }
    }
}
