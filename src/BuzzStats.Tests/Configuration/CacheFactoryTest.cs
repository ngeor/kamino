// --------------------------------------------------------------------------------
// <copyright file="CacheFactoryTest.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/09/16
// * Time: 9:01 μμ
// --------------------------------------------------------------------------------

using NUnit.Framework;
using NGSoftware.Common.Cache;
using BuzzStats.Boot.Web;
using BuzzStats.Configuration;

namespace BuzzStats.Tests.Configuration
{
    [TestFixture]
    public class CacheFactoryTest
    {
        private BuzzStatsConfigurationSection _original;

        [SetUp]
        public void SetUp()
        {
            _original = BuzzStatsConfigurationSection.Current;
            BuzzStatsConfigurationSection.Current = new BuzzStatsConfigurationSection();
        }

        [TearDown]
        public void TearDown()
        {
            BuzzStatsConfigurationSection.Current = _original;
        }

        [Test]
        public void TestCacheDisabled()
        {
            BuzzStatsConfigurationSection.Current.Web = new WebConfigurationElement
            {
                DisableCache = true
            };

            ICache cache = new CacheFactory().Create();
            Assert.IsInstanceOf<NullCache>(cache);
        }

        [Test]
        public void TestCacheEnabled()
        {
            BuzzStatsConfigurationSection.Current.Web = new WebConfigurationElement
            {
                DisableCache = false
            };

            ICache cache = new CacheFactory().Create();
            Assert.IsInstanceOf<HttpRuntimeCache>(cache);
        }
    }
}
