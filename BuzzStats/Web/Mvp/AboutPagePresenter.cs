// --------------------------------------------------------------------------------
// <copyright file="AboutPagePresenter.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/01/24
// * Time: 07:32:38
// --------------------------------------------------------------------------------

using System;
using System.Linq;
using log4net;
using BuzzStats.Data;

namespace BuzzStats.Web.Mvp
{
    public class AboutPagePresenter : ApiServicePresenter<IAboutPageView>
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof(AboutPagePresenter));
        private readonly IDbSession _dbSession;

        public AboutPagePresenter(
            IApiService apiService,
            IDbSession dbSession)
            : base(apiService)
        {
            _dbSession = dbSession;
        }

        protected override void OnViewLoaded(object sender, EventArgs e)
        {
            base.OnViewLoaded(sender, e);

            View.SetOldestCheckedStory(
                ApiService
                    .GetStorySummaries(new GetStorySummariesRequest(StorySortField.LastCheckedAt.Asc(), maxRows: 1))
                    .FirstOrDefault());

            View.SetMinMaxStats(_dbSession.Stories.GetMinMaxStats());
            // TODO SELECT COUNT(*) FROM BuzzStatsLive.Story WHERE RemovedAt IS NULL;
            // TODO SELECT COUNT(*) FROM BuzzStatsLive.Story WHERE RemovedAt IS NOT NULL;
        }
    }
}