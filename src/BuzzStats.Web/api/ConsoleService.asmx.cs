// --------------------------------------------------------------------------------
// <copyright file="ConsoleService.asmx.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/05/27
// * Time: 9:05 πμ
// --------------------------------------------------------------------------------

using System;
using System.Linq;
using System.Reflection;
using System.Web.Script.Services;
using System.Web.Services;
using log4net;
using Microsoft.Practices.ServiceLocation;
using StackExchange.Profiling;
using BuzzStats.Data;

namespace BuzzStats.Web
{
    [ScriptService]
    [WebService(Namespace = "http://ngeor.net/BuzzStats/")]
    public class ConsoleService : WebService, IApiService
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof (ConsoleService));

        public ConsoleService()
        {
            var profiler = MiniProfiler.Current;
            using (profiler.Step("BuildUp"))
            {
                Backend = ServiceLocator.Current.GetInstance<IApiService>();
            }
        }

        public IApiService Backend { get; }

        [WebMethod]
        [ScriptMethod]
        public CountStatsResponse GetCommentCountStats(CountStatsRequest request)
        {
            try
            {
                return Backend.GetCommentCountStats(request);
            }
            catch (Exception ex)
            {
                Log.Error(ex.Message, ex);
                throw;
            }
        }

        [WebMethod]
        [ScriptMethod]
        public HostStats[] GetHostStats(HostStatsRequest options)
        {
            try
            {
                return Backend.GetHostStats(options);
            }
            catch (Exception ex)
            {
                Log.Error(ex.Message, ex);
                throw;
            }
        }

        [WebMethod]
        [ScriptMethod]
        public RecentActivity[] GetRecentActivity(RecentActivityRequest request)
        {
            try
            {
                return Backend.GetRecentActivity(request);
            }
            catch (Exception ex)
            {
                Log.Error(ex.Message, ex);
                throw;
            }
        }

        [WebMethod]
        [ScriptMethod]
        public RecentlyCommentedStory[] GetRecentCommentsPerStory()
        {
            try
            {
                return Backend.GetRecentCommentsPerStory();
            }
            catch (Exception ex)
            {
                Log.Error(ex.Message, ex);
                throw;
            }
        }

        [WebMethod]
        [ScriptMethod]
        public string[] Help()
        {
            return GetType()
                .GetMethods(BindingFlags.DeclaredOnly | BindingFlags.Public | BindingFlags.Instance)
                .Select(m => m.Name)
                .ToArray();
        }

        [WebMethod]
        [ScriptMethod]
        public CountStatsResponse GetStoryCountStats(CountStatsRequest request)
        {
            try
            {
                return Backend.GetStoryCountStats(request);
            }
            catch (Exception ex)
            {
                Log.Error(ex.Message, ex);
                throw;
            }
        }

        [WebMethod]
        [ScriptMethod]
        public string[] Test()
        {
            return new[] {"Tst", "Tosat"};
        }

        [WebMethod, ScriptMethod]
        public CommentSummary[] GetRecentPopularComments()
        {
            return Backend.GetRecentPopularComments();
        }

        [WebMethod]
        [ScriptMethod]
        public StorySummary[] GetStorySummaries(GetStorySummariesRequest request)
        {
            return Backend.GetStorySummaries(request);
        }

        [WebMethod]
        [ScriptMethod]
        public UserStats[] GetUserStats(UserStatsRequest request)
        {
            try
            {
                return Backend.GetUserStats(request);
            }
            catch (Exception ex)
            {
                Log.Error(ex.Message, ex);
                throw;
            }
        }
    }
}
