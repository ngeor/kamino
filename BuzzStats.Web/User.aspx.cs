using System;
using System.Web.UI;
using StackExchange.Profiling;
using BuzzStats.Data;

namespace BuzzStats.Web
{
    public partial class User : Page
    {
        protected void Page_Load(object sender, EventArgs e)
        {
            if (!IsPostBack)
            {
                string user = (Request.QueryString["u"] ?? string.Empty).Trim();
                if (!string.IsNullOrEmpty(user))
                {
                    ShowRecentActivity(user);
                }
            }
        }

        private void ShowRecentActivity(string user)
        {
            lblUser.Text = user;

            var profiler = MiniProfiler.Current;

            using (profiler.Step("GetRecentActivity"))
            {
                repRecentActivity.DataSource =
                    ApiService.GetRecentActivity(new RecentActivityRequest {Username = user});
                repRecentActivity.DataBind();
            }
        }

        public IApiService ApiService { get; set; }
    }
}