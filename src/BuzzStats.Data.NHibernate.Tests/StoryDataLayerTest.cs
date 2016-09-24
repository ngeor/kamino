// --------------------------------------------------------------------------------
// <copyright file="StoryDataLayerTest.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/06/03
// * Time: 8:17 πμ
// --------------------------------------------------------------------------------

using System.Configuration;
using System.Data.Common;
using NUnit.Framework;
using NGSoftware.Common;
using BuzzStats.Data.TestsBase;

namespace BuzzStats.Data.NHibernate.Tests
{
    [TestFixture]
    [Category("Integration")]
    public class StoryDataLayerTest : StoryDataLayerTestBase
    {
        private DbContext _dbContext;
        private DbSession _dbSession;

        public override void TearDown()
        {
            _dbSession.SafeDispose();
            _dbContext.SafeDispose();
            base.TearDown();
        }

        protected override IStoryDataLayer CreateStoryDataLayer(DbConnection dbConnection)
        {
            _dbContext = (DbContext) DbContextFactory.Create(ConfigurationManager.ConnectionStrings["mysql"]);
            _dbSession = (DbSession) _dbContext.OpenSession(dbConnection);
            return new StoryDataLayer(_dbSession.Session);
        }
    }
}
