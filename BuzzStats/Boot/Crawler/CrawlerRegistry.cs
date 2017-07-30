// --------------------------------------------------------------------------------
// <copyright file="CrawlerRegistry.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/09/30
// * Time: 8:26 πμ
// --------------------------------------------------------------------------------

using StructureMap;
using NGSoftware.Common.Messaging;
using BuzzStats.Crawl;
using BuzzStats.Data;
using BuzzStats.Downloader;
using BuzzStats.Persister;

namespace BuzzStats.Boot.Crawler
{
    public class CrawlerRegistry : Registry
    {
        public CrawlerRegistry()
        {
            For<IPersister>().Use<TransactionalPersister>();
            For<IDbPersister>().Use<BasicPersister>();

            For<IDownloaderService>().Use<DownloaderService>();

            // only one messageBus
            For<IMessageBus>().Singleton().Use<MessageBus>();

            For<IWarmCache>().Singleton().Use<WarmCache>()
                .Ctor<IApiService>().Is<IApiService>();
        }
    }
}