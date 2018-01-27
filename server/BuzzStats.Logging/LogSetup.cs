using log4net;
using log4net.Appender;
using log4net.Config;
using log4net.Core;
using log4net.Layout;
using System.Reflection;

namespace BuzzStats.Logging
{
    public static class LogSetup
    {
        public static void Setup()
        {
            var logRepo = LogManager.GetRepository(Assembly.GetEntryAssembly());
            BasicConfigurator.Configure(logRepo, new ConsoleAppender
            {
                Layout = new PatternLayout("%-5level [%thread] %logger - %message%newline"),
                Threshold = Level.Info
            });
        }
    }
}
