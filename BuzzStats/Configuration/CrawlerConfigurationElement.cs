// --------------------------------------------------------------------------------
// <copyright file="CrawlerConfigurationElement.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/09/04
// * Time: 1:27 μμ
// --------------------------------------------------------------------------------

using System;
using System.Configuration;

namespace BuzzStats.Configuration
{
    public class CrawlerConfigurationElement : ConfigurationElement
    {
        /// <summary>
        /// Gets the delay the occurs between network requests in order
        /// to protect foreign hosts from overload during crawling.
        /// </summary>
        [ConfigurationProperty(PropertyNames.NetRequestDelay, IsRequired = false, DefaultValue = "00:00:10")]
        [TimeSpanValidator(MinValueString = "00:00:01", MaxValueString = "00:01:00", ExcludeRange = false)]
        public TimeSpan NetRequestDelay
        {
            get { return (TimeSpan) this[PropertyNames.NetRequestDelay]; }
        }

        [ConfigurationProperty(PropertyNames.Updaters, IsRequired = false)]
        public UpdaterConfigurationElementCollection Updaters
        {
            get { return (UpdaterConfigurationElementCollection) this[PropertyNames.Updaters]; }
            set { this[PropertyNames.Updaters] = value; }
        }

        [ConfigurationProperty("ingesterCount", IsRequired = false, DefaultValue = 0)]
        public int IngesterCount
        {
            get { return (int) this["ingesterCount"]; }
        }

        [ConfigurationProperty(PropertyNames.ListingPages, IsRequired = true)]
        public ListingPagesElementCollection ListingPages
        {
            get { return (ListingPagesElementCollection) this[PropertyNames.ListingPages]; }
        }

        [ConfigurationProperty(PropertyNames.WebServicesPrefix, IsRequired = true)]
        public string WebServicesPrefix
        {
            get { return (string) this[PropertyNames.WebServicesPrefix]; }
        }

        private static class PropertyNames
        {
            public const string NetRequestDelay = "netRequestDelay";
            public const string Updaters = "updaters";
            public const string ListingPages = "listingPages";
            public const string WebServicesPrefix = "webServicesPrefix";
        }
    }
}