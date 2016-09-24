using System;
using System.Web.UI;
using StackExchange.Profiling;
using BuzzStats.Common;
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
                repRecentActivity.DataSource = ApiService.GetRecentActivity(new RecentActivityRequest {Username = user});
                repRecentActivity.DataBind();
            }
        }

        public User()
        {
            ApiService = new ConsoleService();
        }

        public IApiService ApiService { get; set; }

        private string StoryUrl(int storyId, int commentId)
        {
            string result = UrlProvider.StoryUrl(storyId);
            if (commentId > 0)
            {
                result += "#wholecomment" + commentId;
            }

            return result;
        }

        public string StoryUrl(dynamic comment)
        {
            if (comment is RecentActivity)
            {
                RecentActivity recentActivity = (RecentActivity) comment;
                return StoryUrl(recentActivity.StoryId, recentActivity.CommentId);
            }

            return StoryUrl(comment.Story.StoryId, comment.CommentId);
        }

        public string StoryUrl(dynamic story, dynamic comment)
        {
            return StoryUrl(story.StoryId, comment.CommentId);
        }
    }
}
