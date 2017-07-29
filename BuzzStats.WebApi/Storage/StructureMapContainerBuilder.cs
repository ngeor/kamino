using AutoMapper;
using NGSoftware.Common.Configuration;
using NHibernate;
using StructureMap;

namespace BuzzStats.WebApi.Storage
{
    public class StructureMapContainerBuilder
    {
        public IContainer Create()
        {
            Container container = new Container();
            container.Configure(x =>
            {
                x.For<IStoryUpdater>().Use<StoryUpdater>();
                x.For<IStoryVoteUpdater>().Use<StoryVoteUpdater>();
                x.For<ICommentUpdater>().Use<CommentUpdater>();
                x.For<IUpdater>().Use<Updater>();
                x.For<IAppSettings>().Use(() => AppSettingsFactory.DefaultWithEnvironmentOverride());

                // NHibernate
                x.For<ISessionFactoryFactory>().Use<SessionFactoryFactory>().Singleton();
                x.For<ISessionFactory>()
                    .Use(ctx => ctx.GetInstance<ISessionFactoryFactory>().Create())
                    .Singleton();

                // AutoMapper
                x.For<IMapper>().Use(ctx =>
                    new Mapper(ctx.GetInstance<MapperConfiguration>(), ctx.TryGetInstance)).Singleton();
                x.For<MapperConfiguration>().Use(() => CreateMapperConfiguration()).Singleton();
            });

            return container;
        }

        private MapperConfiguration CreateMapperConfiguration() =>
            new MapperConfiguration(cfg => { cfg.AddProfiles(GetType().Assembly); });
    }
}