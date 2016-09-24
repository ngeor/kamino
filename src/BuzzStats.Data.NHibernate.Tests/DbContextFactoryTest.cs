// --------------------------------------------------------------------------------
// <copyright file="DbContextFactoryTest.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/10/17
// * Time: 14:11:19
// --------------------------------------------------------------------------------

using System.Configuration;
using NUnit.Framework;

namespace BuzzStats.Data.NHibernate.Tests
{
    [TestFixture]
    [Category("Integration")]
    public class DbContextFactoryTest
    {
        [Test]
        public void ShouldSupportMySqlProvider()
        {
            var cs = ConfigurationManager.ConnectionStrings["mysql"];
            DbContextFactory dbContextFactory = new DbContextFactory(cs);
            Assert.IsNotNull(dbContextFactory.Create());
        }
    }
}
