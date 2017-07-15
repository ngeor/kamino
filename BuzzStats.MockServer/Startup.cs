using Owin;

namespace BuzzStats.MockServer
{
    public class Startup
    {
        // This code configures Web API. The Startup class is specified as a type
        // parameter in the WebApp.Start method.
        public void Configuration(IAppBuilder appBuilder)
        {
            appBuilder.Use<MockBuzzMiddleware>();
        }
    }
}