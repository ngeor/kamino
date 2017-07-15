using System;
using System.Web;
using BuzzStats.Data;

namespace BuzzStats.Web.Mvp
{
    public class StoryCountControlPresenter : Presenter<ICountControlView>
    {
        private readonly IDbSession _dbSession;

        public StoryCountControlPresenter(IDbSession dbSession)
        {
            _dbSession = dbSession;
        }

        protected override void OnViewLoaded(object sender, EventArgs e)
        {
            base.OnViewLoaded(sender, e);
            View.Count = _dbSession.Stories.Query().Count();
        }
    }
}