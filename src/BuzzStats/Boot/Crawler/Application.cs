// --------------------------------------------------------------------------------
// <copyright file="Application.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/09/30
// * Time: 8:26 πμ
// --------------------------------------------------------------------------------

using System;
using System.Reflection;
using log4net;
using Microsoft.Practices.ServiceLocation;
using StructureMap;

namespace BuzzStats.Boot.Crawler
{
    /// <summary>
    /// Bootstrap class for crawler application.
    /// </summary>
    public static class Application
    {
        private static readonly ILog Log = LogManager.GetLogger(MethodBase.GetCurrentMethod().DeclaringType);

        /// <summary>
        /// Boots the application in crawling mode.
        /// </summary>
        public static IServiceProvider Boot(string[] args)
        {
            Log.Debug("Application.Boot begin");

            SetupErrorHandler(AppDomain.CurrentDomain);

            ObjectFactory.Initialize(_ =>
            {
                _.AddRegistry(new CommonRegistry(args));
                _.AddRegistry<CrawlerRegistry>();
            });
            IServiceProvider result = ServiceLocator.Current;
            Log.Debug("Application.Boot end");
            return result;
        }

        public static void ShutDown()
        {
            AppDomain.CurrentDomain.UnhandledException -= ErrorHandler;
            ObjectFactory.ShutDown();
        }

        public static void SetupErrorHandler(AppDomain appDomain)
        {
            appDomain.UnhandledException += ErrorHandler;
        }

        private static void ErrorHandler(object sender, UnhandledExceptionEventArgs args)
        {
            Exception e = args.ExceptionObject as Exception;
            if (e != null)
            {
                Log.Error(
                    string.Format("{0}, runtime terminating: {1}", e.Message, args.IsTerminating),
                    e);
            }
        }
    }
}
