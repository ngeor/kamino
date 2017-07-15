using System;
using System.Web.UI;
using NGSoftware.Common;

namespace BuzzStats.Web
{
    public partial class Charts : Page
    {
        protected void Page_Load(object sender, EventArgs e)
        {
            if (!IsPostBack)
            {
                // initialize from-to datetime textboxes with default values
                dateRangePicker.Value =
                    DateRange.Create(DateTime.UtcNow.Subtract(TimeSpan.FromDays(7)), DateTime.UtcNow);
            }
        }
    }
}