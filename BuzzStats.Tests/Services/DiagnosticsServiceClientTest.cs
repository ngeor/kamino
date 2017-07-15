using System;
using NUnit.Framework;
using BuzzStats.Services;

namespace BuzzStats.Tests.Services
{
    [TestFixture]
    [Category("Functional")]
    public class DiagnosticsServiceClientTest
    {
        [Test]
        public void ShouldReportUptime()
        {
            DiagnosticsServiceClient client = new DiagnosticsServiceClient();
            TimeSpan result = client.UpTime;
            Assert.Greater(result, TimeSpan.Zero);
        }

        [Test]
        public void ShouldReportEcho()
        {
            DiagnosticsServiceClient client = new DiagnosticsServiceClient();
            string result = client.Echo("test");
            Assert.AreEqual("test", result);
        }
    }
}