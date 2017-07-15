// --------------------------------------------------------------------------------
// <copyright file="AppHelper.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/03/21
// * Time: 16:30:17
// --------------------------------------------------------------------------------

using System;
using System.Linq;
using System.Reflection;
using NGSoftware.Common;
using BuzzStats.Tasks;

namespace BuzzStats.Crawler
{
    class AppHelper
    {
        private Assembly[] GetAssemblies()
        {
            return TypeFinder.Assemblies(typeof(AppContainerAttribute)).ToArray();
        }

        public Type[] GetApps()
        {
            return GetAssemblies().ImplementationsOf(typeof(IApp)).ToArray();
        }

        public int Run(IServiceProvider resolver, string[] args)
        {
            string appName = args.FirstOrDefault(a => !a.StartsWith("-"));
            if (string.IsNullOrWhiteSpace(appName))
            {
                Console.Error.WriteLine("Error: Missing command name");
                ShowHelp();
                return 1;
            }

            Type appType = GetApps().FirstOrDefault(
                t => t.Name.Equals(appName + "App", StringComparison.InvariantCultureIgnoreCase)
                     || t.Name.Equals(appName, StringComparison.InvariantCultureIgnoreCase));

            if (appType == null)
            {
                Console.Error.WriteLine("Error: unknown command: {0}", appName);
                ShowHelp();
                return 2;
            }

            IApp app = (IApp) resolver.GetService(appType);
            app.Run(args.Except(new[] {appName}).ToArray());
            return 0;
        }

        private void ShowHelp()
        {
            new HelpApp().Run(null);
        }
    }
}