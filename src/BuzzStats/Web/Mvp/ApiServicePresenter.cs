using System.Web;
using BuzzStats.Data;

namespace BuzzStats.Web.Mvp
{
    public abstract class ApiServicePresenter<TView> : Presenter<TView>
        where TView : class, IView
    {
        protected ApiServicePresenter(IApiService apiService)
        {
            ApiService = apiService;
        }

        public IApiService ApiService { get; private set; }
    }
}
