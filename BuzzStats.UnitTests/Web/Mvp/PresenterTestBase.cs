using System.Web;
using BuzzStats.Data;
using BuzzStats.Web.Mvp;
using Moq;

namespace BuzzStats.UnitTests.Web.Mvp
{
    public abstract class PresenterTestBase<T> where T : class, IView
    {
        protected Mock<HttpContextBase> mockHttpContext;
        protected Mock<T> mockView;
        protected Mock<IApiService> mockApiService;
        protected Mock<IFormsAuthentication> mockFormsAuthentication;

        protected virtual void PrepareMocks()
        {
            mockHttpContext = new Mock<HttpContextBase>(MockBehavior.Strict);
            mockView = new Mock<T>(MockBehavior.Strict);
            mockApiService = new Mock<IApiService>(MockBehavior.Strict);
            mockFormsAuthentication = new Mock<IFormsAuthentication>(MockBehavior.Strict);
        }

        protected virtual void VerifyMocks()
        {
            mockHttpContext.VerifyAll();
            mockView.VerifyAll();
            mockApiService.VerifyAll();
            mockFormsAuthentication.VerifyAll();
        }
    }
}