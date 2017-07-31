using System;
using AutoMapper;
using BuzzStats.WebApi.Crawl;
using BuzzStats.WebApi.DTOs;
using BuzzStats.WebApi.Parsing;
using BuzzStats.WebApi.Storage;
using BuzzStats.WebApi.Storage.Repositories;
using BuzzStats.WebApi.Storage.Session;
using Castle.DynamicProxy;
using NGSoftware.Common.Configuration;
using NGSoftware.Common.Factories;
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

                // NHibernate and session
                x.For<IFactory<ISessionFactory>>().Use<SessionFactoryFactory>().Singleton();
                x.For<ISessionFactory>()
                    .Use(ctx => ctx.GetInstance<IFactory<ISessionFactory>>().Create())
                    .Singleton();
                x.For<ISessionManager>().Use<SessionManager>().Singleton();

                // repositories
                ConfigureRepository<IStoryRepository, StoryRepository>(x);
                ConfigureRepository<ICommentRepository, CommentRepository>(x);
                ConfigureRepository<IStoryVoteRepository, StoryVoteRepository>(x);
                ConfigureRepository<IRecentActivityRepository, RecentActivityRepository>(x);

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

        private void ConfigureRepository<TInterface, TClass>(ConfigurationExpression x)
            where TInterface : class
            where TClass : TInterface
        {
            x.For<TClass>().Use<TClass>().Ctor<ISession>().Is<LazySession>();
            x.For<TInterface>()
                .Use(ctx => RepositoryInterceptor.Decorate<TInterface>(
                    ctx.GetInstance<TClass>(),
                    ctx.GetInstance<ISessionManager>()
                ));
        }

        private MapperConfiguration CreateMapperConfiguration() =>
            new MapperConfiguration(cfg => { cfg.AddProfiles(GetType().Assembly); });
    }
}
