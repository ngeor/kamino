namespace BuzzStats.Web.Mvp
{
    public interface IFormsAuthentication
    {
        bool Authenticate(string username, string password);
        void RedirectFromLoginPage(string username, bool createPersistentCookie);
    }
}
