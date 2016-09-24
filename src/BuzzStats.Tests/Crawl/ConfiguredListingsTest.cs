// --------------------------------------------------------------------------------
// <copyright file="SourceProviderTest.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/10/17
// * Time: 07:15:50
// --------------------------------------------------------------------------------

using System;
using System.Linq;
using Moq;
using NUnit.Framework;
using BuzzStats.Common;
using BuzzStats.Crawl;
using BuzzStats.Data;
using BuzzStats.Downloader;

namespace BuzzStats.Tests.Crawl
{
    [TestFixture]
    public class ConfiguredListingsTest
    {
        const string url = "http://buzz.reality-tape.com/";
        ConfiguredListings configuredListings;
        Mock<IConfiguration> mockConfiguration;

        [SetUp]
        public void SetUp()
        {
            // arrange
            mockConfiguration = new Mock<IConfiguration>();
            mockConfiguration.Setup(
                p => p.ListingSources).Returns(new[] {url});
            IUrlProvider urlProvider = Mock.Of<IUrlProvider>();
            IDownloaderService downloader = Mock.Of<IDownloaderService>();
            configuredListings = new ConfiguredListings(
                mockConfiguration.Object,
                downloader,
                urlProvider,
                Mock.Of<IDbContext>());
        }

        [Test]
        public void ShouldProvideAllConfiguredListingSources()
        {
            // act
            Listing[] result = configuredListings.GetChildren().Cast<Listing>().ToArray();

            // assert
            Assert.AreEqual(new Uri(url), result[0].Url);
        }

        [Test]
        public void ShouldProvideTheSameObjectEveryTime()
        {
            // act
            Listing result1 = configuredListings.GetChildren().Cast<Listing>().First();
            Listing result2 = configuredListings.GetChildren().Cast<Listing>().First();

            // assert
            Assert.AreSame(result1, result2);
        }

        [Test]
        public void ShouldUseTheUrlAsSourceId()
        {
            // act
            Listing[] result = configuredListings.GetChildren().Cast<Listing>().ToArray();

            // assert
            Assert.AreEqual(url, result[0].SourceId);
        }

        [Test]
        public void ShouldProvideNoSourceWhenSkipIngestersIsOn()
        {
            mockConfiguration.Setup(p => p.SkipIngesters).Returns(true);

            // act
            Listing[] result = configuredListings.GetChildren().Cast<Listing>().ToArray();

            // assert
            Assert.AreEqual(0, result.Length);
        }
    }
}
