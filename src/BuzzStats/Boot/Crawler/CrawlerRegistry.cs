// --------------------------------------------------------------------------------
// <copyright file="CrawlerRegistry.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/09/30
// * Time: 8:26 πμ
// --------------------------------------------------------------------------------

using StructureMap;
using NGSoftware.Common.Cache;
using NGSoftware.Common.Messaging;
using BuzzStats.ApiServices;
using BuzzStats.Crawl;
using BuzzStats.Data;
using BuzzStats.Downloader;
using BuzzStats.Persister;
using BuzzStats.Services;

namespace BuzzStats.Boot.Crawler
{
    public class CrawlerRegistry : Registry
    {
        public CrawlerRegistry()
        {
            For<IPersister>().Use<TransactionalPersister>();
            For<IDbPersister>().Use<BasicPersister>();

            For<ISource>()
                .Add<ConfiguredListings>()
                .Named("ConfiguredListings");

            For<ISource>()
                .Add<ConfiguredPollers>()
                .Named("ConfiguredPollers");

            For<ISource>()
                .Use<AggregateSource>()
                .Ctor<ISource[]>().Is(ctx => new ISource[]
                {
                    ctx.GetInstance<ISource>("ConfiguredListings"),
                    ctx.GetInstance<ISource>("ConfiguredPollers")
                });

            For<IDownloaderService>().Use<DownloaderService>();

            // use singletons for classes that don't really have state
            For<ICache>().Singleton().Use<NullCache>();

            // only one messageBus
            For<IMessageBus>().Singleton().Use<MessageBus>();

            For<IWarmCache>().Singleton().Use<WarmCache>()
                .Ctor<IApiService>().Is<DbContextApiService>();

            For<QueueManager>().Singleton();
            Forward<QueueManager, IQueueManager>();

            For<ILeafProducerMonitor>().Use<LeafProducerMonitor>();

            For<ICrawlerService>().Singleton().Use<CrawlApp>();
        }
    }
}