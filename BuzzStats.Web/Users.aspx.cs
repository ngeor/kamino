using System;
using System.Collections.Generic;
using System.Linq;
using System.Web.UI;
using System.Web.UI.WebControls;
using NGSoftware.Common;
using BuzzStats.Data;

namespace BuzzStats.Web
{
    public partial class Users : Page
    {
        protected void Page_Load(object sender, EventArgs e)
        {
            if (!IsPostBack)
            {
                // initialize from-to datetime textboxes with default values
            }
        }

        protected void gvUsers_RowDataBound(object sender, GridViewRowEventArgs e)
        {
            UserStats userStats = e.Row.DataItem as UserStats;
            if (userStats != null && string.IsNullOrEmpty(userStats.Username))
            {
                e.Row.CssClass = "average";
            }
        }

        protected void repFriends_ItemDataBound(object sender, RepeaterItemEventArgs e)
        {
        }

        protected void gvUsers_RowCommand(object sender, GridViewCommandEventArgs e)
        {
        }

        protected void odsUserStats_Selecting(object sender, ObjectDataSourceSelectingEventArgs e)
        {
        }

        protected void dateRangePicker_Changed(object sender, EventArgs e)
        {
            gvUsers.DataBind();
        }
    }
}