using System;
using System.Collections.Generic;
using System.Net;
using System.Web.Http;
using BuzzStats.StorageWebApi.DTOs;
using BuzzStats.StorageWebApi.Entities;
using log4net;
using NHibernate;

namespace BuzzStats.StorageWebApi
{
    public class StoryController : ApiController
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof(StoryController));
        private readonly ISessionFactory _sessionFactory;
        private readonly StoryMapper _storyMapper;

        public StoryController(ISessionFactory sessionFactory, StoryMapper storyMapper)
        {
            _sessionFactory = sessionFactory;
            _storyMapper = storyMapper;
        }

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
                using (var session = _sessionFactory.OpenSession())
                {
                    var storyEntity = _storyMapper.ToStoryEntity(value);
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
}