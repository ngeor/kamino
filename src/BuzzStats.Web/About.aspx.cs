// --------------------------------------------------------------------------------
// <copyright file="About.aspx.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/05/27
// * Time: 9:05 πμ
// --------------------------------------------------------------------------------

using System;
using BuzzStats.Common;
using BuzzStats.Data;
using BuzzStats.Web.Mvp;

namespace BuzzStats.Web
{
    [Presenter(typeof (AboutPagePresenter))]
    public partial class About : MvpPage, IAboutPageView
    {
        public void SetEchoSucceeded(bool success)
        {
            lblServiceStatus.Text = success ? "OK" : "Error";
        }

        public void SetUpTime(TimeSpan upTime)
        {
            lblUpTime.Text = upTime > TimeSpan.Zero ? upTime.ToString("g") : "N/A";
        }

        public void SetMinMaxStats(MinMaxStats minMaxStats)
        {
            lblMinLastCheckedAt.Text = minMaxStats.LastCheckedAt.Min.ToAgoString();
            lblMaxLastCheckedAt.Text = minMaxStats.LastCheckedAt.Max.ToAgoString();
            lblMinTotalChecks.Text = minMaxStats.TotalChecks.Min.ToString();
            lblMaxTotalChecks.Text = minMaxStats.TotalChecks.Max.ToString();
        }

        public void SetOldestCheckedStory(StorySummary oldestCheckedStory)
        {
            oldestCheckedStories.DataSource = new[] {oldestCheckedStory};
            oldestCheckedStories.DataBind();
        }
    }
}
