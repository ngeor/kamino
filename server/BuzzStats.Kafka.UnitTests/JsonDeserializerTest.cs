using Microsoft.VisualStudio.TestTools.UnitTesting;
using System.Collections.Generic;

namespace BuzzStats.Kafka.UnitTests
{
    [TestClass]
    public class JsonDeserializerTest
    {
        [TestMethod]
        public void TestConfig()
        {
            var deserializer = new JsonDeserializer<JsonDeserializerTest>();
            var config = new Dictionary<string, object>();

            // act
            var result = deserializer.Configure(config, false);

            // assert
            Assert.AreSame(config, result);
        }
    }
}
