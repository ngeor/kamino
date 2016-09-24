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
using System.Web;
using log4net;
using BuzzStats.Data;
using BuzzStats.Services;

namespace BuzzStats.Web.Mvp
{
    public class AboutPagePresenter : ApiServicePresenter<IAboutPageView>
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof (AboutPagePresenter));
        private readonly IDbSession _dbSession;
        private readonly IDiagnosticsService _diagnosticsService;

        public AboutPagePresenter(
            IApiService apiService,
            IDbSession dbSession,
            IDiagnosticsService diagnosticsService)
            : base(apiService)
        {
            _dbSession = dbSession;
            _diagnosticsService = diagnosticsService;
        }

        protected override void OnViewLoaded(object sender, EventArgs e)
        {
            base.OnViewLoaded(sender, e);
            View.SetEchoSucceeded(IsOk(_diagnosticsService));
            View.SetUpTime(GetUpTime(_diagnosticsService));

            View.SetOldestCheckedStory(
                ApiService
                    .GetStorySummaries(new GetStorySummariesRequest(StorySortField.LastCheckedAt.Asc(), maxRows: 1))
                    .FirstOrDefault());

            View.SetMinMaxStats(_dbSession.Stories.GetMinMaxStats());
            // TODO SELECT COUNT(*) FROM BuzzStatsLive.Story WHERE RemovedAt IS NULL;
            // TODO SELECT COUNT(*) FROM BuzzStatsLive.Story WHERE RemovedAt IS NOT NULL;
        }

        bool IsOk(IDiagnosticsService serviceClient)
        {
            try
            {
                const string echoInput = "test123";
                string echoResult = serviceClient.Echo(echoInput);
                return echoInput == echoResult;
            }
            catch (Exception ex)
            {
                Log.Error(ex.Message, ex);
                return false;
            }
        }

        TimeSpan GetUpTime(IDiagnosticsService serviceClient)
        {
            try
            {
                return serviceClient.UpTime;
            }
            catch (Exception ex)
            {
                Log.Error(ex.Message, ex);
                return TimeSpan.Zero;
            }
        }
    }
}
