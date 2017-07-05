using System;

namespace BuzzStats.Web.Mvp
{
    public interface IView
    {
        event EventHandler ViewLoaded;
    }
}