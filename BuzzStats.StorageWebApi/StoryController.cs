using System;
using System.Net;
using System.Web.Http;
using BuzzStats.StorageWebApi.DTOs;
using log4net;
using NHibernate;

namespace BuzzStats.StorageWebApi
{
    public class StoryController : ApiController
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof(StoryController));
        private readonly ISessionFactory _sessionFactory;
        private readonly IUpdater _updater;

        public StoryController(ISessionFactory sessionFactory, IUpdater updater)
        {
            _sessionFactory = sessionFactory;
            _updater = updater;
        }

        // GET api/story/5 
        public Story Get(int id)
        {
            throw new NotImplementedException();
        }

        // POST api/story 
        public void Post([FromBody] Story story)
        {
            if (story == null)
            {
                throw new HttpResponseException(HttpStatusCode.BadRequest);
            }

            Log.InfoFormat("Received story {0} title {1}", story.StoryId, story.Title);

            if (!IsInputValid(story))
            {
                throw new HttpResponseException(HttpStatusCode.BadRequest);
            }

            try
            {
                using (var session = _sessionFactory.OpenSession())
                {
                    _updater.Save(session, story);
                }
            }
            catch (Exception ex)
            {
                Log.Error(ex.Message, ex);
                throw new HttpResponseException(HttpStatusCode.InternalServerError);
            }
        }

        private static bool IsInputValid(Story story)
        {
            // TODO 1. add unit tests 2. limit dates to SQL Server Limitations 
            return !string.IsNullOrWhiteSpace(story.Title) && story.StoryId > 0 && story.CreatedAt != default(DateTime);
        }
    }
}