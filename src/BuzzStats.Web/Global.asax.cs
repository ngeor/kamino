using System;
using System.Web;
using System.Web.Companion;
using log4net;
using StackExchange.Profiling;
using StructureMap.Web.Pipeline;

namespace BuzzStats.Web
{
    /// <summary>
    /// Application class.
    /// </summary>
    public class Global : HttpApplication
    {
        /// <summary>
        /// Our logger.
        /// </summary>
        private static readonly ILog Log = LogManager.GetLogger(typeof(Global));

        /// <summary>
        /// Initializes the application.
        /// </summary>
        /// <param name="sender">Sender of the event.</param>
        /// <param name="e">Event arguments.</param>
        protected virtual void Application_Start(object sender, EventArgs e)
        {
            // auto configures log4net by logging first
            Log.Debug("Application_Start");
            Boot.Web.Application.Boot();
        }

        /// <summary>
        /// Called when a request starts.
        /// </summary>
        /// <param name="sender">Sender of the event.</param>
        /// <param name="e">Event arguments.</param>
        protected virtual void Application_BeginRequest(object sender, EventArgs e)
        {
            Log.DebugFormat("Begin Request {0}", Request.Url);

            if (UseMiniProfiler())
            {
                MiniProfiler.Start();
            }
        }

        /// <summary>
        /// Called when a request ends.
        /// </summary>
        /// <param name="sender">Sender of the event.</param>
        /// <param name="e">Event arguments.</param>
        protected virtual void Application_EndRequest(object sender, EventArgs e)
        {
            try
            {
                Log.DebugFormat("Ending Request {0}", HttpContext.Current.Request.Url);
            }
            catch
            {
                Log.Warn("Could not log end request msg");
            }

            try
            {
                HttpContextLifecycle.DisposeAndClearAll();
            }
            catch (Exception ex)
            {
                Log.Warn("Could not release http objects", ex);
            }

            if (UseMiniProfiler())
            {
                try
                {
                    MiniProfiler.Stop();
                }
                catch
                {
                    Log.Warn("Could not stop mini profiler");
                }
            }
        }

        /// <summary>
        /// Called when an unhandled exception occurs.
        /// </summary>
        /// <param name="sender">Sender of the event.</param>
        /// <param name="e">Event arguments.</param>
        protected virtual void Application_Error(object sender, EventArgs e)
        {
            try
            {
                HttpContextLifecycle.DisposeAndClearAll();
            }
            catch (Exception ex)
            {
                Log.Warn("Could not release http objects", ex);
            }

            Log.DebugFormat("Referer: '{0}', UserAgent: '{1}'", GetReferer(), GetUserAgent());
            this.LogException();
        }

        private string GetReferer()
        {
            try
            {
                return Request.UrlReferrer.ToString();
            }
            catch
            {
                return string.Empty;
            }
        }

        private string GetUserAgent()
        {
            try
            {
                return Request.UserAgent;
            }
            catch
            {
                return string.Empty;
            }
        }

        private bool UseMiniProfiler()
        {
            return Request.IsLocal && HttpContext.Current.IsDebuggingEnabled;
        }
    }
}