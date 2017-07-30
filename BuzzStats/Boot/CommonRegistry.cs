// --------------------------------------------------------------------------------
// <copyright file="CommonRegistry.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2014
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/09/04
// * Time: 1:27 μμ
// --------------------------------------------------------------------------------

using System.Configuration;
using StructureMap;
using NGSoftware.Common;
using NGSoftware.Common.Configuration;
using NGSoftware.Common.Factories;
using NGSoftware.Common.Scheduling;
using BuzzStats.Common;
using BuzzStats.Crawl;
using BuzzStats.Data;
using BuzzStats.Data.NHibernate;

namespace BuzzStats.Boot
{
    public class CommonRegistry : Registry
    {
        public CommonRegistry(string[] args)
        {
            For<IStoppable>().Singleton().Use<Stoppable>();
            For<IBackgroundScheduledItemManager>().Singleton().Use<BackgroundScheduledItemManager>();

            For<ConnectionStringSettings>().Use(ctx => ConfigurationManager.ConnectionStrings["BuzzStats"]);
            For<IFactory<IDbContext>>().Singleton().Use<DbContextFactory>();

            // needs to be singleton, it owns the ISessionFactory actually
            For<IDbContext>().Singleton().Use(ctx => ctx.GetInstance<IFactory<IDbContext>>().Create());
        }
    }
}