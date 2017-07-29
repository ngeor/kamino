using System;
using System.Net;
using System.Web.Http;
using BuzzStats.WebApi.DTOs;
using log4net;
using NHibernate;

namespace BuzzStats.WebApi.Storage
{
    public class StorageClient : IStorageClient
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof(StorageClient));
        
        private readonly ISessionFactory _sessionFactory;
        private readonly IUpdater _updater;

        public StorageClient(ISessionFactory sessionFactory, IUpdater updater)
        {
            _sessionFactory = sessionFactory;
            _updater = updater;
        }
        
        public virtual void Save(Story story)
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