using System;
using System.Collections.Generic;
using System.Net;
using System.Threading.Tasks;
using System.Web.Http;
using Microsoft.Owin;
using Microsoft.Owin.Hosting;
using Owin;

namespace BuzzStats.MockServer
{
    internal class Program
    {
        public static void Main(string[] args)
        {
            const string baseAddress = "http://localhost:9900/";

            // Start OWIN host 
            using (WebApp.Start<Startup>(url: baseAddress)) 
            {
                Console.WriteLine("Server listening at port 9900");
                Console.ReadLine(); 
            }
        }
    }
    
    public class Startup
    {
        // This code configures Web API. The Startup class is specified as a type
        // parameter in the WebApp.Start method.
        public void Configuration(IAppBuilder appBuilder)
        {
            appBuilder.Use<MyMiddleware>();
        }
    }

    public class MyMiddleware : OwinMiddleware
    {
        public MyMiddleware(OwinMiddleware next) : base(next)
        {
        }

        public override async Task Invoke(IOwinContext context)
        {
            if (!context.Request.Path.Value.Equals("/hello"))
            {
                await Next.Invoke(context);
                return;
            }

            context.Response.StatusCode = (int) HttpStatusCode.OK;
            context.Response.ContentType = "text/html";
            context.Response.Write("<html><body><h1>Test</h1><p>Hi</p></body></html>");
        }
    }
}