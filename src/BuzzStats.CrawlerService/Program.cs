using System;
using System.Collections.Generic;
using System.Web.Http;
using Microsoft.Owin.Hosting;
using Owin;

namespace BuzzStats.CrawlerService
{
    internal class Program
    {
        public static void Main(string[] args)
        {
            const string baseAddress = "http://localhost:9001/";

            // Start OWIN host 
            using (WebApp.Start<Startup>(url: baseAddress)) 
            {
                Console.WriteLine("Server listening at port 9001");
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
            // Configure Web API for self-host. 
            HttpConfiguration config = new HttpConfiguration();
            config.Routes.MapHttpRoute(
                name: "DefaultApi",
                routeTemplate: "api/{controller}/{id}",
                defaults: new {id = RouteParameter.Optional}
            );

            appBuilder.UseWebApi(config);
        }
    }
    
    public class ValuesController : ApiController 
    { 
        // GET api/values 
        public IEnumerable<string> Get() 
        { 
            return new[] { "value1", "value2" }; 
        } 

        // GET api/values/5 
        public string Get(int id) 
        { 
            return "value"; 
        } 

        // POST api/values 
        public void Post([FromBody]string value) 
        { 
        } 

        // PUT api/values/5 
        public void Put(int id, [FromBody]string value) 
        { 
        } 

        // DELETE api/values/5 
        public void Delete(int id) 
        { 
        } 
    }
}