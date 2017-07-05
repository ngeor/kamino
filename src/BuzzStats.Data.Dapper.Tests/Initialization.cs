using System.Diagnostics;
using MySql.Data.MySqlClient;
using NUnit.Framework;

namespace BuzzStats.Data.Dapper.Tests
{
    [SetUpFixture]
    public class Initialization
    {
        [TestFixtureSetUp]
        public void SetUp()
        {
            // debug MySQL in output window
            var mySwitch = new SourceSwitch("My switch", "Verbose");
            var listener = new ConsoleTraceListener() {Name = "Console"};
            MySqlTrace.Switch = mySwitch;
            MySqlTrace.Listeners.Clear();
            MySqlTrace.Listeners.Add(listener);
        }
    }
}