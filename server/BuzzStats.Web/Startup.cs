using AutoMapper;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Hosting;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;

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

            services.AddSingleton<IMapper>(_ =>
                new Mapper(CreateMapperConfiguration()));

            services.AddSingleton<IRepository, MongoRepository>();
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
        }
    }
}