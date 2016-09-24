using System;

namespace BuzzStats.Web.Admin
{
    public partial class TestPage : System.Web.UI.Page
    {
        protected void btnThrow_Click(object sender, EventArgs e)
        {
            throw new NotSupportedException("Test exception");
        }

        protected void Page_Load(object sender, EventArgs e)
        {
            if (!IsPostBack)
            {
            }
        }
    }
}
