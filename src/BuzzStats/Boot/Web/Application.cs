// --------------------------------------------------------------------------------
// <copyright file="Application.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/09/30
// * Time: 8:26 πμ
// --------------------------------------------------------------------------------
using System;
using Microsoft.Practices.ServiceLocation;

namespace BuzzStats.Boot.Web
{
    /// <summary>
    /// Bootstrapping class for web application.
    /// </summary>
    public static class Application
    {
        /// <summary>
        /// Boots the application in web mode.
        /// </summary>
        /// <remarks>
        /// Invocation is actually done through an attribute in Global.asax.cs:
        /// <c>[assembly: WebActivatorEx.PreApplicationStartMethod(typeof(Application), "Boot")]</c>
        /// So don't rename or refactor.
        /// Also don't log in here with log4net.
        /// </remarks>
        public static IServiceProvider Boot()
        {
            ObjectFactory.Initialize(x =>
            {
                x.AddRegistry(new CommonRegistry(new string[0]));
                x.AddRegistry<WebRegistry>();
            });
            IServiceProvider result = ServiceLocator.Current;
            return result;
        }

        public static void ShutDown()
        {
            ObjectFactory.ShutDown();
        }
    }
}
