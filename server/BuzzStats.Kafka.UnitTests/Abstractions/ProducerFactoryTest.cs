using System.Collections.Generic;
using BuzzStats.Kafka.Abstractions;
using Confluent.Kafka;
using FluentAssertions;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Yak.Kafka;

namespace BuzzStats.Kafka.UnitTests.Abstractions
{
    [TestClass]
    public class ProducerFactoryTest
    {
        [TestMethod]
        public void Create()
        {
            var config = new Dictionary<string, object>
            {
                ["bootstrap.servers"] = "broker"
            };

            var producerFactory = new ProducerFactory<Null, string>();
            var serializer = Serializers.String();

            // act
            using (var producer = producerFactory.Create(config, null, serializer))
            {
                // assert
                producer.KeySerializer.Should().BeNull();
                producer.ValueSerializer.Should().Be(serializer);
            }
        }
    }
}
