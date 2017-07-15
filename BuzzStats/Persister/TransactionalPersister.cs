using System;
using BuzzStats.Data;
using BuzzStats.Parsing;

namespace BuzzStats.Persister
{
    public sealed class TransactionalPersister : IPersister
    {
        private readonly IDbContext _dbContext;
        private readonly IDbPersister _backend;

        public TransactionalPersister(IDbContext dbContext, IDbPersister backend)
        {
            if (backend == null)
            {
                throw new ArgumentNullException("backend", "Backend persister was null");
            }
            _dbContext = dbContext;
            _backend = backend;
        }

        public PersisterResult MarkAsUnmodified(int storyId)
        {
            return _dbContext.RunInTransaction(dbSession =>
            {
                try
                {
                    _backend.DbSession = dbSession;
                    return _backend.MarkAsUnmodified(storyId);
                }
                finally
                {
                    _backend.DbSession = null;
                }
            });
        }

        public PersisterResult Save(Story story)
        {
            return _dbContext.RunInTransaction(dbSession =>
            {
                try
                {
                    _backend.DbSession = dbSession;
                    return _backend.Save(story);
                }
                finally
                {
                    _backend.DbSession = null;
                }
            });
        }
    }
}