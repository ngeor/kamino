using Autofac;
using AutoMapper;
using BuzzStats.Web.Mongo;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Hosting;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using System;
using System.Threading.Tasks;
using Yak.Configuration.Autofac;

namespace BuzzStats.Web
{
    public class Startup
    {
        public Startup(IConfiguration configuration)
        {
            Configuration = configuration;
        }

        public IConfiguration Configuration { get; }

        private MapperConfiguration CreateMapperConfiguration() =>
            new MapperConfiguration(cfg => { cfg.AddProfiles(GetType().Assembly); });

        // This method gets called by the runtime. Use this method to add services to the container.
        public void ConfigureServices(IServiceCollection services)
        {
            services.AddMvcCore()
                .AddJsonFormatters()
                .AddCors(c =>
                {
                    c.AddPolicy("Default", b => b.AllowAnyOrigin().AllowAnyMethod().AllowAnyHeader());
                    c.DefaultPolicyName = "Default";
                });
        }

        public void ConfigureContainer(ContainerBuilder builder)
        {
            builder.RegisterType<Program>()
                .InjectConfiguration();

            builder.RegisterType<ConsumerBuilder>()
                .InjectConfiguration();

            builder.Register(c => c.Resolve<ConsumerBuilder>().Build());
            builder.RegisterType<Repository>().As<IRepository>().InjectConfiguration();
            builder.Register(c => new Mapper(CreateMapperConfiguration()))
                .As<IMapper>()
                .SingleInstance();
        }

        // This method gets called by the runtime. Use this method to configure the HTTP request pipeline.
        public void Configure(IApplicationBuilder app, IHostingEnvironment env)
        {
            if (env.IsDevelopment())
            {
                app.UseDeveloperExceptionPage();
            }

            app.UseMvc()
                .UseCors("Default");

            Program.Instance = app.ApplicationServices.GetRequiredService<Program>();
            
        }
    }
}