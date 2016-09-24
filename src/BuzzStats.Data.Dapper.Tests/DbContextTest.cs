using System.Configuration;
using System.Data.Common;
using NUnit.Framework;
using NGSoftware.Common;
using BuzzStats.Data.TestsBase;

namespace BuzzStats.Data.Dapper.Tests
{
    [TestFixture]
    [Category("Integration")]
    public class DbContextTest
    {
        private DbConnection _connection;

        [TearDown]
        public void TearDown()
        {
            _connection.SafeDispose();
        }

        [Test]
        public void TestMySqlConnectivity()
        {
            _connection = ConfigurationManager.ConnectionStrings["mysql"].CreateConnection();
            _connection.Open();
        }

        [Test]
        public void CanInitializeDatabase()
        {
            _connection = ConfigurationManager.ConnectionStrings["mysql"].CreateConnection();
            _connection.Open();
            _connection.PrepareDatabase("sample.sql");
        }

        [Test]
        public void TestDbContext()
        {
            DbContext dbContext = new DbContext();
            var session = dbContext.OpenSession();
            Assert.IsNotNull(session);
            session.Dispose();
            dbContext.Dispose();
        }
    }
}
