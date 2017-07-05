// --------------------------------------------------------------------------------
// <copyright file="HelpApp.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/03/21
// * Time: 16:30:14
// --------------------------------------------------------------------------------

using System;
using BuzzStats.Tasks;

namespace BuzzStats.Crawler
{
    [CommandLine("", "prints this help message")]
    class HelpApp : IApp
    {
        public void Run(string[] args)
        {
            Console.WriteLine("Usage:");
            foreach (var appType in new AppHelper().GetApps())
            {
                string commandName = appType.Name.ToLowerInvariant();
                if (commandName.EndsWith("app"))
                {
                    commandName = commandName.Substring(0, commandName.Length - "app".Length);
                }

                var cla = CommandLineAttribute.GetAttribute(appType);
                Console.WriteLine("BuzzStats.Crawler {0} {1}", commandName, cla.Flags);
                Console.WriteLine("\t{0}", cla.Help);
            }
        }
    }
}