using System.Web.Security;

namespace BuzzStats.Web.Mvp
{
    public class FormsAuthenticationWrapper : IFormsAuthentication
    {
        public bool Authenticate(string username, string password)
        {
            return FormsAuthentication.Authenticate(username, password);
        }

        public void RedirectFromLoginPage(string username, bool createPersistentCookie)
        {
            FormsAuthentication.RedirectFromLoginPage(username, createPersistentCookie);
        }
    }
}
