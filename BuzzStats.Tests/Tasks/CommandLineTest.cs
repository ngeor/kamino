using NUnit.Framework;
using BuzzStats.Tasks;

namespace BuzzStats.Tests.Tasks
{
    [TestFixture]
    public class CommandLineTest
    {
        [Test]
        public void TestOnlyCommand()
        {
            string[] args = new[] {"crawl"};
            var result = CommandLine.Parse(args);
            Assert.IsNotNull(result);
            Assert.AreEqual("crawl", result.Command);
        }

        [Test]
        public void TestEmpty()
        {
            string[] args = new string[0];
            var result = CommandLine.Parse(args);
            Assert.IsNotNull(result);
            Assert.AreEqual(string.Empty, result.Command);
        }

        [Test]
        public void TestBoolFlag()
        {
            string[] args = new[] {"crawl", "-nohost"};
            var result = CommandLine.Parse(args);
            Assert.IsNotNull(result);
            Assert.AreEqual("crawl", result.Command);
            Assert.IsTrue(result.Flags["nohost"]);
        }
    }
}