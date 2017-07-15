using System;
using NUnit.Framework;
using BuzzStats.Web.Mvp;

namespace BuzzStats.Tests.Web.Mvp
{
    [TestFixture]
    public class LoginPagePresenterTest : PresenterTestBase<ILoginPageView>
    {
        [Test]
        public void TestSuccess()
        {
            PrepareMocks();

            mockView.SetupGet(v => v.Username).Returns("nikolaos");
            mockView.SetupGet(v => v.Password).Returns("42");

            mockFormsAuthentication.Setup(p => p.Authenticate("nikolaos", "42")).Returns(true);
            mockFormsAuthentication.Setup(p => p.RedirectFromLoginPage("nikolaos", false));

            LoginPagePresenter presenter = new LoginPagePresenter(mockFormsAuthentication.Object)
            {
                View = mockView.Object
            };
            mockView.Raise(v => v.ViewLoaded += null, EventArgs.Empty);
            mockView.Raise(v => v.LoginRequested += null, EventArgs.Empty);

            VerifyMocks();
        }

        [Test]
        public void TestFail()
        {
            PrepareMocks();

            mockView.SetupGet(v => v.Username).Returns("nikolaos");
            mockView.SetupGet(v => v.Password).Returns("42");
            mockView.Setup(v => v.LoginFailed());

            mockFormsAuthentication.Setup(p => p.Authenticate("nikolaos", "42")).Returns(false);

            LoginPagePresenter presenter = new LoginPagePresenter(mockFormsAuthentication.Object)
            {
                View = mockView.Object
            };
            mockView.Raise(v => v.ViewLoaded += null, EventArgs.Empty);
            mockView.Raise(v => v.LoginRequested += null, EventArgs.Empty);

            VerifyMocks();
        }
    }
}