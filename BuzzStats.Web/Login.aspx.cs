using System;
using BuzzStats.Web.Mvp;

namespace BuzzStats.Web
{
    [Presenter(typeof(LoginPagePresenter))]
    public partial class Login : MvpPage, ILoginPageView
    {
        public event EventHandler LoginRequested;

        public string Username
        {
            get { return txtUsername.Text; }
        }

        public string Password
        {
            get { return txtPassword.Text; }
        }

        public void LoginFailed()
        {
            lblLoginFailed.Visible = true;
        }

        protected void btnSubmit_Click(object sender, EventArgs e)
        {
            lblLoginFailed.Visible = false;

            if (IsValid)
            {
                if (LoginRequested != null)
                {
                    LoginRequested(this, EventArgs.Empty);
                }
            }
        }
    }
}