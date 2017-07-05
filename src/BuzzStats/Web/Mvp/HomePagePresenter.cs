using System;
using System.Linq;
using System.Web;
using log4net;
using BuzzStats.Common;
using BuzzStats.Data;

namespace BuzzStats.Web.Mvp
{
    public class HomePagePresenter : ApiServicePresenter<IHomePageView>
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof(HomePagePresenter));

        public HomePagePresenter(
            IApiService apiService,
            IUrlProvider urlProvider)
            : base(apiService)
        {
            this.UrlProvider = urlProvider;
        }

        private IUrlProvider UrlProvider { get; set; }

        protected override void OnViewLoaded(object sender, EventArgs e)
        {
            base.OnViewLoaded(sender, e);
            Log.Debug("Enter ViewLoaded");
            View.RecentActivities = ApiService.GetRecentActivity(null).Select(
                r => new RecentActivityModel(r, UrlProvider.StoryUrl(r.StoryId, r.CommentId))).ToArray();
            View.RecentlyCommentedStories = ApiService.GetRecentCommentsPerStory();
            View.RecentPopularComments = ApiService.GetRecentPopularComments();
            Log.Debug("Exit ViewLoaded");
        }
    }
}