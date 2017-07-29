using System.Web.Http;
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
            
            // Add StructureMap dependency injection
            config.DependencyResolver = new StructureMapDependencyResolver(CreateContainer());
            
            // Add routes
            config.Routes.MapHttpRoute(
                name: "DefaultApi",
                routeTemplate: "api/{controller}/{id}",
                defaults: new {id = RouteParameter.Optional}
            );

            appBuilder.UseWebApi(config);
        }

        private IContainer CreateContainer() => new StructureMapContainerBuilder().Create();
    }
}