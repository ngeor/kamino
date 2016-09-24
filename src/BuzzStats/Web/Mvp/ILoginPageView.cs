using System;

namespace BuzzStats.Web.Mvp
{
    public interface ILoginPageView : IView
    {
        event EventHandler LoginRequested;

        string Username { get; }

        string Password { get; }

        void LoginFailed();
    }
}
