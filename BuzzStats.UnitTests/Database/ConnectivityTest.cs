// --------------------------------------------------------------------------------
// <copyright file="ConnectivityTest.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/10/17
// * Time: 13:30:23
// --------------------------------------------------------------------------------

using System;
using System.Data;
using System.Data.Common;
using System.Linq;
using NUnit.Framework;

namespace BuzzStats.UnitTests.Database
{
    [TestFixture]
    public class ConnectivityTest
    {
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
    }
}