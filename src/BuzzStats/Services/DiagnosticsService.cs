using System;
using System.Reflection;
using log4net;
using NGSoftware.Common.WebServices;

namespace BuzzStats.Services
{
    [WebService]
    public class DiagnosticsService : IDiagnosticsService
    {
        private static readonly ILog Log = LogManager.GetLogger(
            MethodBase.GetCurrentMethod().DeclaringType);

        public DiagnosticsService(ICrawlerService crawlerService)
        {
            Log.DebugFormat("{0} constructor", GetType().Name);
            CrawlerService = crawlerService;
        }

        public TimeSpan UpTime
        {
            get { return CrawlerService.GetUpTime(); }
        }

        private ICrawlerService CrawlerService { get; set; }

        public string Echo(string message)
        {
            return message;
        }
    }
}