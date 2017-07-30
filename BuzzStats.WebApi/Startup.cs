using System.Web.Http;
using BuzzStats.WebApi.IoC;
using Newtonsoft.Json.Serialization;
using Owin;

namespace BuzzStats.WebApi
{
    public class Startup
    {
        // This code configures Web API. The Startup class is specified as a type
        // parameter in the WebApp.Start method.
        public void Configuration(IAppBuilder appBuilder)
        {
            // Configure Web API for self-host. 
            HttpConfiguration config = new HttpConfiguration();
            config.DependencyResolver = new StructureMapDependencyResolver(ContainerHolder.Container);

            config.Routes.MapHttpRoute(
                name: "DefaultApi",
                routeTemplate: "api/{controller}/{id}",
                defaults: new {id = RouteParameter.Optional}
            );

            var jsonFormatter = config.Formatters.JsonFormatter;
            var serializerSettings = jsonFormatter.SerializerSettings;
            serializerSettings.ContractResolver = new CamelCasePropertyNamesContractResolver();

            appBuilder.UseWebApi(config);
            appBuilder.UseFileServer();
        }
    }
}