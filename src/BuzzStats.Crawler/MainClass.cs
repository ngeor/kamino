// --------------------------------------------------------------------------------
// <copyright file="MainClass.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/05/27
// * Time: 9:05 πμ
// --------------------------------------------------------------------------------

using log4net;
using Microsoft.Practices.ServiceLocation;
using BuzzStats.Boot.Crawler;

namespace BuzzStats.Crawler
{
    public class MainClass
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof (MainClass));

        public static int Main(string[] args)
        {
            Log.Debug("MainClass.Main");
            Application.Boot(args);
            AppHelper appHelper = new AppHelper();
            return appHelper.Run(ServiceLocator.Current, args);
        }
    }
}
