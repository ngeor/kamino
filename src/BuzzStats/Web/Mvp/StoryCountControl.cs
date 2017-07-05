using System.Web.UI;

namespace BuzzStats.Web.Mvp
{
    [Presenter(typeof(StoryCountControlPresenter))]
    public class StoryCountControl : MvpControl, ICountControlView
    {
        public int Count { get; set; }

        protected override void Render(HtmlTextWriter writer)
        {
            base.Render(writer);
            writer.Write(Count.ToString("N0"));
        }
    }
}