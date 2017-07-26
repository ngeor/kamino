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
using NGSoftware.Common.Factories;
using BuzzStats.ApiServices;
using BuzzStats.Configuration;
using BuzzStats.Data;
using BuzzStats.Services;
using BuzzStats.Web.Mvp;

namespace BuzzStats.Boot.Web
{
    public class WebRegistry : Registry
    {
        public WebRegistry()
        {
            // use singletons for classes that don't really have state
            if (BuzzStatsConfigurationSection.Current.Web.DisableCache)
            {
                For<ICache>().Singleton().Use<NullCache>();
            }
            else
            {
                For<ICache>().Singleton().Use<HttpRuntimeCache>();
            }

            For<IApiService>()
                .Add<ApiService>()
                .Named("uncached");

            For<IApiService>()
                .Add<WebClientApiService>()
                .Ctor<IApiService>().Is(ctx => ctx.GetInstance<IApiService>("uncached"))
                .Named("webapi-uncached");

            For<IApiService>()
                .Add<CachedApiService>()
                .Ctor<IFactory<IApiService>>().Is(
                    ctx => new ResolverFactory<IApiService>("webapi-uncached"))
                .Named("cached");

            For<IApiService>()
                .Use<LoggingApiServiceDecorator>()
                .Ctor<IApiService>().Is(ctx => ctx.GetInstance<IApiService>("cached"));

            For<IDbSession>().Use(ctx => ctx.GetInstance<IDbContext>().OpenSession());
            For<IFormsAuthentication>().Singleton().Use<FormsAuthenticationWrapper>();

            For<IDiagnosticsService>().Use<DiagnosticsServiceClient>();
            For<IRecentActivityService>().Use<RecentActivityServiceClient>();
        }
    }
}