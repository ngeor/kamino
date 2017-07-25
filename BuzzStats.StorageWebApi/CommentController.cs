using System;
using System.Collections.Generic;
using System.Linq;
using System.Web.Http;
using BuzzStats.StorageWebApi.DTOs;
using BuzzStats.StorageWebApi.Entities;
using log4net;
using NHibernate;
using NHibernate.Criterion;

namespace BuzzStats.StorageWebApi
{
    public class CommentController : ApiController
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof(CommentController));
        private readonly ISessionFactory _sessionFactory;

        public CommentController(ISessionFactory sessionFactory)
        {
            _sessionFactory = sessionFactory;
        }
        
        // GET api/comment
        public IEnumerable<CommentWithStory> Get()
        {
            try
            {
                using (var session = _sessionFactory.OpenSession())
                {
                    var criteria = session.CreateCriteria<CommentEntity>();
                    criteria = criteria.SetMaxResults(20);
                    criteria = criteria.AddOrder(Order.Desc("CreatedAt"));
                    return criteria.List<CommentEntity>().Select(c => new CommentWithStory(c)).ToList();
                }
            }
            catch (Exception ex)
            {
                Log.Error(ex.Message, ex);
                throw;
            }
        }
    }
}