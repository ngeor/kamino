// --------------------------------------------------------------------------------
// <copyright file="StoriesPagePresenter.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2014
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2014/08/09
// * Time: 19:21:08
// --------------------------------------------------------------------------------

using System;
using System.Linq;
using System.Web;
using NGSoftware.Common;
using BuzzStats.Data;
using NodaTime;

namespace BuzzStats.Web.Mvp
{
    public class StoriesPagePresenter : ApiServicePresenter<IStoriesPageView>
    {
        public StoriesPagePresenter(IApiService apiService)
            : base(apiService)
        {
        }

        protected override void OnViewLoaded(object sender, EventArgs e)
        {
            base.OnViewLoaded(sender, e);
            View.SetCommentCount(ApiService.GetCommentCountStats(new CountStatsRequest
            {
                Interval = PeriodUnits.Years
            }).Data.Sum());

            foreach (StorySortField storySortField in
                new[]
                {
                    StorySortField.LastModifiedAt, StorySortField.CreatedAt, StorySortField.LastCommentedAt
                })
            {
                View.SetStories(
                    storySortField,
                    ApiService.GetStorySummaries(new GetStorySummariesRequest(storySortField.Desc(), maxRows: 10)));
            }
        }
    }
}