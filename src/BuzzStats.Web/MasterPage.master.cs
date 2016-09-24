using System;
using System.Configuration;
using System.Web.Companion;
using System.Web.Companion.UI;
using StackExchange.Profiling;

namespace BuzzStats.Web
{
    [SiteTitlePrefix("ngeor.net | BuzzStats", " | ")]
    public partial class MasterPage : CompanionMasterPage
    {
        protected override void OnLoad(EventArgs e)
        {
            var profiler = MiniProfiler.Current;
            using (profiler.Step("MasterPage.OnLoad"))
            {
                base.OnLoad(e);
                Page.Header.DataBind();
            }
        }

        protected bool IsLive()
        {
            return "live".Equals(ConfigurationManager.AppSettings["environment"],
                StringComparison.InvariantCultureIgnoreCase);
        }

        private bool IsCurrentPage(string path)
        {
            if (string.IsNullOrEmpty(path))
            {
                return IsCurrentPage("Default.aspx");
            }

            return Request.Url.AbsolutePath.EndsWith(path, StringComparison.OrdinalIgnoreCase);
        }

        private string GetMenuLinkCss(string path)
        {
            return IsCurrentPage(path) ? " class=\"active\"" : "";
        }

        protected string MenuLink(string path, string title)
        {
            return string.Format(@"<a href=""{0}""{1}>{2}</a>",
                Request.SlashedAppPath() + path,
                GetMenuLinkCss(path),
                title);
        }
    }
}
