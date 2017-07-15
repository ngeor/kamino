using System;
using System.Web;
using System.Web.SessionState;
using Newtonsoft.Json;

namespace BuzzStats.Web.api
{
    /// <summary>
    /// Summary description for WebSocketTest
    /// </summary>
    public class CrawlerEventsJs : IHttpHandler, IRequiresSessionState
    {
        public void ProcessRequest(HttpContext context)
        {
            context.Response.ContentType = "application/json";
            var msg = MessageStack.Instance.Latest;
            if (msg == null)
            {
                context.Response.Write(JsonConvert.SerializeObject(new
                {
                    idx = 0
                }));
            }
            else
            {
                context.Response.Write(JsonConvert.SerializeObject(new
                {
                    message = msg,
                    timestamp = DateTime.UtcNow,
                    idx = 0
                }));
            }
        }

        public bool IsReusable
        {
            get { return false; }
        }
    }
}