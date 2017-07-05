// --------------------------------------------------------------------------------
// <copyright file="SourceProvider.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/10/17
// * Time: 07:16:50
// --------------------------------------------------------------------------------

using System;
using System.Collections.Generic;
using System.Linq;
using BuzzStats.Common;
using BuzzStats.Data;
using BuzzStats.Downloader;

namespace BuzzStats.Crawl
{
    public class ConfiguredListings : ISource
    {
        private readonly IConfiguration _configuration;
        private readonly IDownloaderService _downloader;
        private readonly IUrlProvider _urlProvider;
        private readonly IDbContext _dbContext;
        private readonly Lazy<IEnumerable<ISource>> _children;

        public ConfiguredListings(
            IConfiguration configuration,
            IDownloaderService downloader,
            IUrlProvider urlProvider,
            IDbContext dbContext)
        {
            if (configuration == null)
            {
                throw new ArgumentNullException("configuration");
            }

            _configuration = configuration;

            if (downloader == null)
            {
                throw new ArgumentNullException("downloader");
            }

            _downloader = downloader;

            if (urlProvider == null)
            {
                throw new ArgumentNullException("urlProvider");
            }

            _urlProvider = urlProvider;

            if (dbContext == null)
            {
                throw new ArgumentNullException("dbContext");
            }

            _dbContext = dbContext;
            _children = new Lazy<IEnumerable<ISource>>(DoGetChildren);
        }

        public IEnumerable<ISource> GetChildren()
        {
            return _children.Value;
        }

        private IEnumerable<ISource> DoGetChildren()
        {
            if (_configuration.SkipIngesters)
            {
                return Enumerable.Empty<ISource>();
            }

            return _configuration.ListingSources.Select(CreateListingSource).ToArray();
        }

        private ISource CreateListingSource(string url)
        {
            return new Listing(_downloader, _urlProvider, url, _dbContext);
        }
    }
}