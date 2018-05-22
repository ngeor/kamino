using System.Collections.Generic;
using BuzzStats.Kafka.Abstractions;
using Confluent.Kafka;
using Confluent.Kafka.Serialization;
using FluentAssertions;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;
using Yak.Kafka;

namespace BuzzStats.Kafka.UnitTests.Abstractions
{
    [TestClass]
    public class ProducerBuilderTest
    {
        [TestMethod]
        public void BuildWithoutKey()
        {
            var producerFactoryMock = new Mock<IProducerFactory<Null, string>>();
            var serializer = Serializers.String();
            var builder = new ProducerBuilder("broker")
                .WithValueSerializerAndNoKey(serializer)
                .WithProducerFactory(producerFactoryMock.Object);
            var expectedConfig = new Dictionary<string, object>
            {
                ["bootstrap.servers"] = "broker"
            };

            producerFactoryMock.Setup(v => v.Create(
                expectedConfig,
                null,
                serializer
            )).Returns(new Producer<Null, string>(expectedConfig, null, serializer));

            // act
            using (var producer = builder.Build())
            {
                // assert
                producer.Should().NotBeNull();
                producerFactoryMock.Verify(v => v.Create(expectedConfig, null, serializer));
            }
        }

        [TestMethod]
        public void BuildWithKey()
        {
            var producerFactoryMock = new Mock<IProducerFactory<string, string>>();
            var keySerializer = Serializers.String();
            var valueSerializer = Serializers.String();
            var builder = new ProducerBuilder("broker")
                .WithKeySerializer(keySerializer)
                .WithValueSerializer(valueSerializer)
                .WithProducerFactory(producerFactoryMock.Object);
            var expectedConfig = new Dictionary<string, object>
            {
                ["bootstrap.servers"] = "broker"
            };

            producerFactoryMock.Setup(v => v.Create(
                expectedConfig,
                keySerializer,
                valueSerializer
            )).Returns(new Producer<string, string>(expectedConfig, keySerializer, valueSerializer));

            // act
            using (var producer = builder.Build())
            {
                // assert
                producer.Should().NotBeNull();
                producerFactoryMock.Verify(v => v.Create(expectedConfig, keySerializer, valueSerializer));
            }
        }
    }
}
