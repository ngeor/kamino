using System;
using System.Linq;
using System.Web;
using Microsoft.Practices.ServiceLocation;

namespace BuzzStats.Web.Mvp
{
    internal class PresenterStrategy
    {
        private readonly IView _uiElement;

        public PresenterStrategy(MvpPage page)
        {
            _uiElement = page;
        }

        public PresenterStrategy(MvpControl control)
        {
            _uiElement = control;
        }

        public virtual void CreatePresenter()
        {
            var presenterType = GetPresenterType();
            var serviceLocator = ServiceLocator.Current;
            if (serviceLocator == null)
            {
                throw new InvalidOperationException("ServiceLocator not initialized!");
            }

            var presenter = serviceLocator.GetInstance(presenterType) as Presenter;
            if (presenter == null)
            {
                throw new InvalidOperationException("Unsupported presenter: " + presenterType);
            }

            presenter.HttpContext = new HttpContextWrapper(HttpContext.Current);
            presenter.View = _uiElement;
        }

        private Type GetPresenterType()
        {
            return _uiElement.GetType().GetCustomAttributes(typeof(PresenterAttribute), true)
                .OfType<PresenterAttribute>().Single().PresenterType;
        }
    }
}