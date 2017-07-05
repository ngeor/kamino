// --------------------------------------------------------------------------------
// <copyright file="Configuration.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/10/17
// * Time: 08:39:03
// --------------------------------------------------------------------------------

using System;
using System.Collections.Generic;
using System.Linq;
using BuzzStats.Configuration;

namespace BuzzStats.Crawl
{
    public class Configuration : IConfiguration
    {
        private readonly HashSet<string> _argsMap = new HashSet<string>(StringComparer.InvariantCultureIgnoreCase);

        /// <summary>
        /// Initializes a new instance of the <see cref="Configuration"/> class.
        /// </summary>
        /// <param name="args">Command line arguments that can override the config file.</param>
        public Configuration(params string[] args)
        {
            foreach (var arg in args)
            {
                _argsMap.Add(arg);
            }
        }

        public string[] ListingSources
        {
            get
            {
                return
                    BuzzStatsConfigurationSection.Current.Crawler.ListingPages.Cast<ListingPageConfigurationElement>()
                        .Select(e => e.Url).ToArray();
            }
        }

        public bool SkipIngesters
        {
            get { return _argsMap.Contains("-skipIngesters"); }
        }

        public bool SkipPollers
        {
            get { return _argsMap.Contains("-skipPollers"); }
        }
    }
}