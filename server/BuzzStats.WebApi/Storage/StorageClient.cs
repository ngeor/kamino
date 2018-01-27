using System;
using System.Collections.Generic;
using System.Linq;
using AutoMapper;
using BuzzStats.DTOs;
using BuzzStats.WebApi.DTOs;
using BuzzStats.WebApi.Storage.Repositories;
using BuzzStats.WebApi.Storage.Session;
using log4net;

namespace BuzzStats.WebApi.Storage
{
    /// <summary>
    /// The single point of entry for accessing persisted data.
    /// This class is responsible for opening a new session for each requested operation.
    /// </summary>
    public class StorageClient : IStorageClient
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof(StorageClient));

        private readonly ISessionManager _sessionManager;
        private readonly IMapper _mapper;
        private readonly IUpdater _updater;
        private readonly ICommentRepository _commentRepository;
        private readonly IRecentActivityRepository _recentActivityRepository;

        public StorageClient(ISessionManager sessionManager, IMapper mapper, IUpdater updater,
            ICommentRepository commentRepository, IRecentActivityRepository recentActivityRepository)
        {
            _sessionManager = sessionManager;
            _mapper = mapper;
            _updater = updater;
            _commentRepository = commentRepository;
            _recentActivityRepository = recentActivityRepository;
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
                throw new ArgumentException();
            }

            try
            {
                using (var session = _sessionManager.Create())
                {
                    _updater.Save(session, story);
                }
            }
            catch (Exception ex)
            {
                Log.Error(ex.Message, ex);
                throw;
            }
        }

        public IList<CommentWithStory> GetRecentComments()
        {
            using (var session = _sessionManager.Create())
            {
                var commentEntities = _commentRepository.GetRecent();
                return commentEntities.Select(e => _mapper.Map<CommentWithStory>(e)).ToList();
            }
        }

        public IList<RecentActivity> GetRecentActivity()
        {
            using (var session = _sessionManager.Create())
            {
                var recentActivityEntities = _recentActivityRepository.Get();
                return recentActivityEntities.Select(e => _mapper.Map<RecentActivity>(e)).ToList();
            }
        }

        private static bool IsInputValid(Story story)
        {
            // TODO 1. add unit tests 2. limit dates to SQL Server Limitations
            return !string.IsNullOrWhiteSpace(story.Title) && story.StoryId > 0 && story.CreatedAt != default(DateTime);
        }
    }
}
