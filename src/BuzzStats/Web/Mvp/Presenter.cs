using System;
using System.Web;

namespace BuzzStats.Web.Mvp
{
    public abstract class Presenter
    {
        protected internal HttpContextBase HttpContext { get; set; }

        protected HttpResponseBase Response
        {
            get { return HttpContext.Response; }
        }

        protected HttpRequestBase Request
        {
            get { return HttpContext.Request; }
        }

        protected IView _view;

        protected internal IView View
        {
            get { return _view; }

            set
            {
                if (_view != null)
                {
                    UnSubscribeFromView(_view);
                }

                _view = value;

                if (_view != null)
                {
                    SubscribeToView(_view);
                }
            }
        }

        protected virtual void SubscribeToView(IView view)
        {
            view.ViewLoaded += OnViewLoaded;
        }

        protected virtual void UnSubscribeFromView(IView view)
        {
            View.ViewLoaded -= OnViewLoaded;
        }

        protected virtual void OnViewLoaded(object sender, EventArgs e)
        {
        }
    }

    public abstract class Presenter<TView> : Presenter
        where TView : class, IView
    {
        protected internal new TView View
        {
            get { return (TView) base.View; }

            set { base.View = value; }
        }

        protected override void SubscribeToView(IView view)
        {
            base.SubscribeToView(view);
            SubscribeToView((TView) view);
        }

        protected override void UnSubscribeFromView(IView view)
        {
            UnSubscribeFromView((TView) view);
            base.UnSubscribeFromView(view);
        }

        protected virtual void SubscribeToView(TView view)
        {
        }

        protected virtual void UnSubscribeFromView(TView view)
        {
        }
    }
}