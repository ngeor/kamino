using System;

namespace BuzzStats.Web.Mvp
{
    [AttributeUsage(AttributeTargets.Class, AllowMultiple = false)]
    public class PresenterAttribute : Attribute
    {
        public PresenterAttribute(Type presenterType)
        {
            PresenterType = presenterType;
        }

        public Type PresenterType { get; private set; }
    }
}
