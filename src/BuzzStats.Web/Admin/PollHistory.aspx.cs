using System;

namespace BuzzStats.Web.Admin
{
    public partial class PollHistory : System.Web.UI.Page
    {
        protected int TotalCount { get; private set; }

        protected void Page_Load(object sender, EventArgs e)
        {
            if (IsPostBack)
            {
                return;
            }
        }
    }
}