using BuzzStats.Data;

namespace BuzzStats.Web.Mvp
{
    public interface IHomePageView : IView
    {
        RecentlyCommentedStory[] RecentlyCommentedStories { set; }
        RecentActivityModel[] RecentActivities { set; }
        CommentSummary[] RecentPopularComments { set; }
    }
}