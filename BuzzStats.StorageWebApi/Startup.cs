using System.Web.Http;
using NGSoftware.Common.Configuration;
using NHibernate;
using Owin;
using StructureMap;

namespace BuzzStats.StorageWebApi
{
    public class Startup
    {
        // This code configures Web API. The Startup class is specified as a type
        // parameter in the WebApp.Start method.
        public void Configuration(IAppBuilder appBuilder)
        {
            // Configure Web API for self-host. 
            HttpConfiguration config = new HttpConfiguration();
            config.DependencyResolver = new StructureMapDependencyResolver(CreateContainer());
            config.Routes.MapHttpRoute(
                name: "DefaultApi",
                routeTemplate: "api/{controller}/{id}",
                defaults: new {id = RouteParameter.Optional}
            );

            appBuilder.UseWebApi(config);
        }

        private IContainer CreateContainer()
        {
            Container container = new Container();
            container.Configure(x =>
            {
                x.For<IStoryUpdater>().Use<StoryUpdater>();
                x.For<IStoryVoteUpdater>().Use<StoryVoteUpdater>();
                x.For<ICommentUpdater>().Use<CommentUpdater>();
                x.For<IUpdater>().Use<Updater>();
                x.For<ISessionFactoryFactory>().Use<SessionFactoryFactory>().Singleton();
                x.For<ISessionFactory>()
                    .Use(ctx => ctx.GetInstance<ISessionFactoryFactory>().Create())
                    .Singleton();
                x.For<IAppSettings>().Use(() => AppSettingsFactory.DefaultWithEnvironmentOverride());
            });
            
            return container;
        }
    }
}