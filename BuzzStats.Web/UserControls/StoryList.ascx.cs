// --------------------------------------------------------------------------------
// <copyright file="StoryList.ascx.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/09/04
// * Time: 1:27 μμ
// --------------------------------------------------------------------------------

using System.Web.UI;

namespace BuzzStats.Web.UserControls
{
    public partial class StoryList : UserControl
    {
        public object DataSource
        {
            get { return repStories.DataSource; }
            set { repStories.DataSource = value; }
        }

        public string DataSourceID
        {
            get { return repStories.DataSourceID; }
            set { repStories.DataSourceID = value; }
        }
    }
}