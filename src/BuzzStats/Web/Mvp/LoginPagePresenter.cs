using System;
using System.Web;

namespace BuzzStats.Web.Mvp
{
    public class LoginPagePresenter : Presenter<ILoginPageView>
    {
        public LoginPagePresenter(IFormsAuthentication formsAuthentication)
        {
            FormsAuthentication = formsAuthentication;
        }

        protected IFormsAuthentication FormsAuthentication { get; private set; }

        protected override void SubscribeToView(ILoginPageView view)
        {
            base.SubscribeToView(view);
            view.LoginRequested += OnLoginRequested;
        }

        protected override void UnSubscribeFromView(ILoginPageView view)
        {
            base.UnSubscribeFromView(view);
            view.LoginRequested -= OnLoginRequested;
        }

        private void OnLoginRequested(object sender, EventArgs e)
        {
            if (FormsAuthentication.Authenticate(View.Username, View.Password))
            {
                FormsAuthentication.RedirectFromLoginPage(View.Username, false);
            }
            else
            {
                View.LoginFailed();
            }
        }
    }
}
