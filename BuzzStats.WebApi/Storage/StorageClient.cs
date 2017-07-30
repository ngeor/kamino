using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Web.Http;
using AutoMapper;
using BuzzStats.WebApi.DTOs;
using BuzzStats.WebApi.Storage.Repositories;
using log4net;
using NHibernate;

namespace BuzzStats.WebApi.Storage
{
    public class StorageClient : IStorageClient
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof(StorageClient));
        
        private readonly ISessionFactory _sessionFactory;
        private readonly IUpdater _updater;
        private readonly CommentRepository _commentRepository;
        private readonly IMapper _mapper;

        public StorageClient(ISessionFactory sessionFactory, IUpdater updater, CommentRepository commentRepository, IMapper mapper)
        {
            _sessionFactory = sessionFactory;
            _updater = updater;
            _commentRepository = commentRepository;
            _mapper = mapper;
        }
        
        public void Save(Story story)
        {
            if (story == null)
            {
                throw new ArgumentNullException(nameof(story));
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

        public IList<CommentWithStory> GetRecentComments()
        {
            using (var session = _sessionFactory.OpenSession())
            {
                var commentEntities = _commentRepository.GetRecent(session);
                return commentEntities.Select(e => _mapper.Map<CommentWithStory>(e)).ToList();
            }
        }

        private static bool IsInputValid(Story story)
        {
            // TODO 1. add unit tests 2. limit dates to SQL Server Limitations 
            return !string.IsNullOrWhiteSpace(story.Title) && story.StoryId > 0 && story.CreatedAt != default(DateTime);
        }
    }
}