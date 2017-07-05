namespace BuzzStats.Data.Dapper
{
    public class DbContext : IDbContext
    {
        public IDbSession OpenSession()
        {
            return new DbSession();
        }

        public void Dispose()
        {
        }
    }
}