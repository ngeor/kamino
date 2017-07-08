using System;
using System.Collections.Generic;
using System.Net;
using System.Web.Http;
using BuzzStats.StorageWebApi.DTOs;
using BuzzStats.StorageWebApi.Entities;
using FluentNHibernate.Cfg;
using FluentNHibernate.Cfg.Db;
using log4net;
using NHibernate;
using NHibernate.Cfg;
using NHibernate.Tool.hbm2ddl;

namespace BuzzStats.StorageWebApi
{
    public class StoryController : ApiController
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof(StoryController));

        // GET api/story 
        public IEnumerable<string> Get()
        {
            return new[] {"value1", "value2"};
        }

        // GET api/story/5 
        public string Get(int id)
        {
            return "value";
        }

        // POST api/story 
        public void Post([FromBody] Story value)
        {
            Log.InfoFormat("Received story {0} title {1}", value.StoryId, value.Title);
            try
            {
                using (var session = SessionFactoryHolder.SessionFactory.OpenSession())
                {
                    StoryEntity storyEntity = new StoryEntity
                    {
                        Title = value.Title,
                        StoryId = value.StoryId,
                        Username = value.Username,
                        Url = value.Url,
                        Category = value.Category,
                    };
                    session.SaveOrUpdate(storyEntity);
                    session.Flush();
                }
            }
            catch (Exception ex)
            {
                Log.Error(ex.Message, ex);
                throw new HttpResponseException(HttpStatusCode.InternalServerError);
            }
        }

        // PUT api/story/5 
        public void Put(int id, [FromBody] string value)
        {
        }

        // DELETE api/story/5 
        public void Delete(int id)
        {
        }
    }

    static class SessionFactoryHolder
    {
        private static readonly Lazy<ISessionFactory> sessionFactory = new Lazy<ISessionFactory>(StartDb);

        internal static ISessionFactory SessionFactory => sessionFactory.Value;

        private static ISessionFactory StartDb()
        {
            var sessionFactory = Fluently.Configure()
                .Database(MySQLConfiguration.Standard.ConnectionString(c => c.FromConnectionStringWithKey("BuzzStats")))
                .Mappings(m => m.FluentMappings.AddFromAssemblyOf<StoryController>())
                .ExposeConfiguration(BuildSchema)
                .BuildSessionFactory();
            return sessionFactory;
        }

        private static void BuildSchema(Configuration cfg)
        {
            new SchemaExport(cfg).Create(true, true);
        }
    }
}