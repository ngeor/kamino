//
//  MySqlRecentActivityTest.cs
//
//  Author:
//       ngeor
//
//  Copyright (c) 2014 ngeor

using System;
using System.Configuration;
using System.Linq;
using NUnit.Framework;

namespace BuzzStats.Data.NHibernate.Tests
{
    [TestFixture]
    [Category("Integration")]
    public class MySqlRecentActivityTest
    {
        [Test]
        public void SqliteReturnsNullRecentActivityRepository()
        {
            var connectionParameters = ConfigurationManager.ConnectionStrings["sqlite"];
            using (var dbContext = DbContextFactory.Create(connectionParameters))
            {
                using (var dbSession = dbContext.OpenSession())
                {
                    Assert.Throws<NotSupportedException>(() => { var x = dbSession.RecentActivityRepository; });
                }
            }
        }

        [Test]
        public void MysqlSupportsRecentActivityRepository()
        {
            var connectionParameters = ConfigurationManager.ConnectionStrings["mysql"];
            using (var dbContext = DbContextFactory.Create(connectionParameters))
            {
                using (var dbSession = dbContext.OpenSession())
                {
                    var recentActivityRepository = dbSession.RecentActivityRepository;
                    Assert.IsNotNull(recentActivityRepository);
                    var result = recentActivityRepository.Get(new RecentActivityRequest
                    {
                        MaxCount = 10
                    });

                    Assert.IsNotNull(result);
                    Assert.Greater(result.Length, 0);
                    Assert.IsTrue(result.All(r => r.StoryId > 0));
                }
            }
        }

        [Test]
        public void MysqlSupportsRecentActivityRepository_WithUsername()
        {
            var connectionParameters = ConfigurationManager.ConnectionStrings["mysql"];
            using (var dbContext = DbContextFactory.Create(connectionParameters))
            {
                using (var dbSession = dbContext.OpenSession())
                {
                    var recentActivityRepository = dbSession.RecentActivityRepository;
                    Assert.IsNotNull(recentActivityRepository);
                    var result = recentActivityRepository.Get(new RecentActivityRequest
                    {
                        MaxCount = 10,
                        Username = "ngeor"
                    });

                    Assert.IsNotNull(result);
                    Assert.Greater(result.Length, 0);
                    Assert.IsTrue(result.All(r => r.StoryId > 0));
                    Assert.IsTrue(result.All(r => r.Who == "ngeor"));
                }
            }
        }
    }
}
