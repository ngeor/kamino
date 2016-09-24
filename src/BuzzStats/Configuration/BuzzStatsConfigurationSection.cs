// --------------------------------------------------------------------------------
// <copyright file="BuzzStatsConfigurationSection.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/09/04
// * Time: 1:27 μμ
// --------------------------------------------------------------------------------

using System.Configuration;

namespace BuzzStats.Configuration
{
    /// <summary>
    /// Configuration section for BuzzStats.
    /// </summary>
    public class BuzzStatsConfigurationSection : ConfigurationSection
    {
        private const string SectionName = "buzzStats";

        private static BuzzStatsConfigurationSection _current;

        public static BuzzStatsConfigurationSection Current
        {
            get { return _current ?? (_current = LoadCurrent()); }

            set { _current = value; }
        }

        [ConfigurationProperty("connectionStringName", DefaultValue = "BuzzStats", IsRequired = false)]
        public string ConnectionStringName
        {
            get { return (string) this["connectionStringName"]; }
            set { this["connectionStringName"] = value; }
        }

        /// <summary>
        /// Gets or sets the crawler configuration section.
        /// </summary>
        /// <value>The crawler.</value>
        [ConfigurationProperty("crawler")]
        public CrawlerConfigurationElement Crawler
        {
            get { return (CrawlerConfigurationElement) this["crawler"]; }
            set { this["crawler"] = value; }
        }

        /// <summary>
        /// Gets or sets web-specific configuration.
        /// </summary>
        [ConfigurationProperty("web")]
        public WebConfigurationElement Web
        {
            get { return (WebConfigurationElement) this["web"]; }
            set { this["web"] = value; }
        }

        private static BuzzStatsConfigurationSection LoadCurrent()
        {
            BuzzStatsConfigurationSection current =
                (BuzzStatsConfigurationSection) ConfigurationManager.GetSection(SectionName);
            return current ?? new BuzzStatsConfigurationSection();
        }
    }
}
