using System.Net;
using System.Threading.Tasks;
using Microsoft.Owin;

namespace BuzzStats.MockServer
{
    public class MockBuzzMiddleware : OwinMiddleware
    {
        public MockBuzzMiddleware(OwinMiddleware next) : base(next)
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