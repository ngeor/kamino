using System;

namespace BuzzStats.Data
{
    public static class DbContextExtensions
    {
        public static void RunInTransaction(this IDbContext dbContext, Action<IDbSession> action)
        {
            using (IDbSession dbSession = dbContext.OpenSession())
            {
                dbSession.BeginTransaction();
                try
                {
                    action(dbSession);
                    dbSession.Commit();
                }
                catch
                {
                    dbSession.Rollback();
                    throw;
                }
            }
        }

        public static T RunInTransaction<T>(this IDbContext dbContext, Func<IDbSession, T> action)
        {
            T result;
            using (IDbSession dbSession = dbContext.OpenSession())
            {
                dbSession.BeginTransaction();
                try
                {
                    result = action(dbSession);
                    dbSession.Commit();
                }
                catch
                {
                    dbSession.Rollback();
                    throw;
                }
            }

            return result;
        }
    }
}