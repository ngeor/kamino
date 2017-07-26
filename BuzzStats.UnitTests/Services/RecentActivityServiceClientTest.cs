// --------------------------------------------------------------------------------
// <copyright file="RecentActivityServiceClientTest.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/12/18
// * Time: 15:33:55
// --------------------------------------------------------------------------------

using BuzzStats.Services;
using NUnit.Framework;

namespace BuzzStats.UnitTests.Services
{
    [TestFixture]
    [Category("Functional")]
    public class RecentActivityServiceClientTest
    {
        [Test]
        public void ShouldGetRecentActivity()
        {
            RecentActivityServiceClient client = new RecentActivityServiceClient();
            var result = client.GetRecentActivity();
            Assert.IsNotNull(result);
        }
    }
}