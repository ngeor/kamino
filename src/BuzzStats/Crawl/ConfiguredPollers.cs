// --------------------------------------------------------------------------------
// <copyright file="ConfiguredPollers.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/10/22
// * Time: 05:49:06
// --------------------------------------------------------------------------------

using System;
using System.Collections.Generic;
using System.Linq;
using NGSoftware.Common.Messaging;
using BuzzStats.Common;
using BuzzStats.Data;

namespace BuzzStats.Crawl
{
    public class ConfiguredPollers : ISource
    {
        private readonly IMessageBus _messageBus;
        private readonly IDbContext _dbContext;
        private readonly IUrlProvider _urlProvider;
        private readonly IConfiguration _configuration;
        private readonly ILeafProducerMonitor _leafProducerMonitor;
        private readonly Lazy<IEnumerable<ISource>> _children;

        public ConfiguredPollers(
            IMessageBus messageBus,
            IDbContext dbContext,
            IUrlProvider urlProvider,
            IConfiguration configuration,
            ILeafProducerMonitor leafProducerMonitor)
        {
            _messageBus = messageBus;
            _dbContext = dbContext;
            _urlProvider = urlProvider;
            _configuration = configuration;
            _leafProducerMonitor = leafProducerMonitor;
            _children = new Lazy<IEnumerable<ISource>>(DoGetChildren);
        }

        public IEnumerable<ISource> GetChildren()
        {
            return _children.Value;
        }

        private IEnumerable<ISource> DoGetChildren()
        {
            if (_configuration.SkipPollers)
            {
                return Enumerable.Empty<ISource>();
            }

            return new[]
            {
                new Poller(
                    _messageBus,
                    _dbContext,
                    _urlProvider,
                    _leafProducerMonitor)
            };
        }
    }
}
