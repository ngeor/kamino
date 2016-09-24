// --------------------------------------------------------------------------------
// <copyright file="ConnectivityTest.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/10/17
// * Time: 13:30:23
// --------------------------------------------------------------------------------

using System;
using System.Configuration;
using System.Data;
using System.Data.Common;
using System.Linq;
using NUnit.Framework;
using BuzzStats.Data;

namespace BuzzStats.Tests.Database
{
    [TestFixture]
    public class ConnectivityTest
    {
        [Test]
        [Category("Integration")]
        public void ShouldConnectToTheConfiguredDatabase()
        {
            var cs = ConfigurationManager.ConnectionStrings["BuzzStats"];
            DbProviderFactory dbProviderFactory = DbProviderFactories.GetFactory(cs.ProviderName);
            using (DbConnection connection = dbProviderFactory.CreateConnection())
            {
                connection.ConnectionString = cs.ConnectionString;
                connection.Open();
            }
        }

        [Test]
        [Category("Integration")]
        public void ShouldPrintAvailableDbProviderFactories()
        {
            var fc = DbProviderFactories.GetFactoryClasses();
            var rows = fc.Rows.Cast<DataRow>();
            foreach (var row in rows)
            {
                foreach (DataColumn c in fc.Columns)
                {
                    Console.Write(row[c.Ordinal]);
                    Console.Write(";");
                }

                Console.WriteLine();
            }
        }

        [Test]
        public void ShouldCreateTheDatabaseWhenAppSettingIsSet()
        {
            Assert.IsTrue(ConfigurationManager.ConnectionStrings["Test1"].ShouldCreateDb());
        }

        [Test]
        public void ShouldNotCreateTheDatabaseWhenAppSettingIsNotSet()
        {
            Assert.IsFalse(ConfigurationManager.ConnectionStrings["Test2"].ShouldCreateDb());
        }

        [Test]
        public void ShouldShowSqlWhenAppSettingIsSet()
        {
            Assert.IsTrue(ConfigurationManager.ConnectionStrings["Test2"].ShouldShowSql());
        }

        [Test]
        public void ShouldNotShowSqlWhenAppSettingIsNotSet()
        {
            Assert.IsFalse(ConfigurationManager.ConnectionStrings["Test1"].ShouldShowSql());
        }
    }
}
