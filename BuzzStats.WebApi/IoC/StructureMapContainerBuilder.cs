using System;
using AutoMapper;
using BuzzStats.WebApi.Crawl;
using BuzzStats.WebApi.DTOs;
using BuzzStats.WebApi.Parsing;
using BuzzStats.WebApi.Storage;
using NGSoftware.Common.Configuration;
using NGSoftware.Common.Messaging;
using NHibernate;
using NodaTime;
using StructureMap;

namespace BuzzStats.WebApi.IoC
{
    public class StructureMapContainerBuilder
    {
        public IContainer Create()
        {
            Container container = new Container();
            container.Configure(x =>
            {
                // storage
                x.For<IStoryUpdater>().Use<StoryUpdater>();
                x.For<IStoryVoteUpdater>().Use<StoryVoteUpdater>();
                x.For<ICommentUpdater>().Use<CommentUpdater>();
                x.For<IUpdater>().Use<Updater>();

                // settings
                x.For<IAppSettings>().Use(() => AppSettingsFactory.DefaultWithEnvironmentOverride());

                // parsing
                x.For<IUrlProvider>().Use<UrlProvider>();
                x.For<IParser>().Use<Parser>();

                // crawl
                x.For<IAsyncQueue<StoryListingSummary>>()
                    .Use(() => new AsyncQueue<StoryListingSummary>(TimeSpan.FromMinutes(1)))
                    .Singleton();

                x.For<IStoryProcessTopic>().Use<StoryProcessTopic>();

                // clients
                x.For<IParserClient>().Use<ParserClient>();
                x.For<IStorageClient>().Use<StorageClient>();

                // NHibernate
                x.For<ISessionFactoryFactory>().Use<SessionFactoryFactory>().Singleton();
                x.For<ISessionFactory>()
                    .Use(ctx => ctx.GetInstance<ISessionFactoryFactory>().Create())
                    .Singleton();

                // AutoMapper
                x.For<IMapper>().Use(ctx =>
                    new Mapper(ctx.GetInstance<MapperConfiguration>(), ctx.TryGetInstance)).Singleton();
                x.For<MapperConfiguration>().Use(() => CreateMapperConfiguration()).Singleton();

                // other
                x.For<IMessageBus>().Use<MessageBus>().Singleton();
                x.For<IClock>().Use(() => SystemClock.Instance).Singleton();
            });

            return container;
        }

        private MapperConfiguration CreateMapperConfiguration() =>
            new MapperConfiguration(cfg => { cfg.AddProfiles(GetType().Assembly); });
    }
}
