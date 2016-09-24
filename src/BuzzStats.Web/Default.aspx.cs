using System.Web.UI.WebControls;
using BuzzStats.Data;
using BuzzStats.Web.Mvp;

namespace BuzzStats.Web
{
    [Presenter(typeof (HomePagePresenter))]
    public partial class Default : MvpPage, IHomePageView
    {
        protected void repRecentComments_ItemDataBound(object sender, RepeaterItemEventArgs e)
        {
            Repeater repComments = e.Item.FindControl("repComments") as Repeater;
            if (repComments != null)
            {
                RecentlyCommentedStory recentStory = (RecentlyCommentedStory) e.Item.DataItem;
                if (recentStory != null)
                {
                    repComments.DataSource = recentStory.Comments;
                    repComments.DataBind();
                }
            }
        }

        public RecentlyCommentedStory[] RecentlyCommentedStories
        {
            set
            {
                repRecentComments.DataSource = value;
                repRecentComments.DataBind();
            }
        }

        public RecentActivityModel[] RecentActivities
        {
            set
            {
                repRecentActivity.DataSource = value;
                repRecentActivity.DataBind();
            }
        }

        public CommentSummary[] RecentPopularComments
        {
            set
            {
                repRecentPopularComments.DataSource = value;
                repRecentPopularComments.DataBind();
            }
        }

        protected string DiffBetweenCreatedAndDetected(RecentActivity recentActivity)
        {
            if (recentActivity == null || recentActivity.DetectedAtAge == recentActivity.Age)
            {
                return string.Empty;
            }

            return string.Format(
                Common.Resources.DiffBetweenDetectedAtAndCreatedAtIs_X,
                (int) (recentActivity.Age - recentActivity.DetectedAtAge).TotalMinutes);
        }

        public static class MyResources
        {
            public static string What(object what)
            {
                return Common.Resources.ResourceManager.GetString("What" + what);
            }
        }
    }
}
