using System;
using System.Web.UI;

namespace BuzzStats.Web.Mvp
{
    public class MvpPage : Page, IView
    {
        public event EventHandler ViewLoaded;

        protected override void OnLoad(EventArgs e)
        {
            base.OnLoad(e);
            CreatePresenter();
            if (ViewLoaded != null)
            {
                ViewLoaded(this, EventArgs.Empty);
            }
        }

        protected virtual void CreatePresenter()
        {
            new PresenterStrategy(this).CreatePresenter();
        }
    }
}