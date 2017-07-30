// --------------------------------------------------------------------------------
// <copyright file="WebRegistry.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/09/30
// * Time: 8:26 πμ
// --------------------------------------------------------------------------------

using StructureMap;
using NGSoftware.Common.Cache;
using BuzzStats.ApiServices;
using BuzzStats.Data;

namespace BuzzStats.Boot.Web
{
    public class WebRegistry : Registry
    {
        public WebRegistry()
        {
            // use singletons for classes that don't really have state
            For<ICache>().Singleton().Use<NullCache>();

            For<IApiService>()
                .Use<ApiService>();

            For<IDbSession>().Use(ctx => ctx.GetInstance<IDbContext>().OpenSession());
        }
    }
}