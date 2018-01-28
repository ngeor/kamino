using FluentAssertions;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System.Collections.Generic;

namespace BuzzStats.Kafka.UnitTests
{
    [TestClass]
    public class JsonSerializerTest
    {
        [TestMethod]
        public void CanSerialize()
        {
            var serializer = new JsonSerializer<Demo>();
            var deserializer = new JsonDeserializer<Demo>();
            var demo = new Demo
            {
                Name = "hello",
                Age = 24
            };

            // act
            var result = deserializer.Deserialize("whatever",
                serializer.Serialize("whatever", demo));

            // assert
            result.ShouldBeEquivalentTo(demo);
        }

        [TestMethod]
        public void TestConfig()
        {
            var serializer = new JsonSerializer<JsonDeserializerTest>();
            var config = new Dictionary<string, object>();

            // act
            var result = serializer.Configure(config, false);

            // assert
            Assert.AreSame(config, result);
        }

        class Demo
        {
            public string Name { get; set; }
            public int Age { get; set; }
        }
    }
}
