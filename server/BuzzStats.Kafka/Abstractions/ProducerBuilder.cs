using System;
using System.Collections.Generic;
using Confluent.Kafka;
using Confluent.Kafka.Serialization;

namespace BuzzStats.Kafka.Abstractions
{
    public class ProducerBuilder
    {
        public ProducerBuilder(string brokerList)
        {
            BrokerList = brokerList ?? throw new ArgumentNullException(nameof(brokerList));
        }

        public string BrokerList { get; }

        public ProducerBuilder<TKey> WithKeySerializer<TKey>(ISerializer<TKey> keySerializer)
        {
            return new ProducerBuilder<TKey>(BrokerList, keySerializer);
        }

        public ProducerBuilder<Null, TValue> WithValueSerializerAndNoKey<TValue>(ISerializer<TValue> valueSerializer)
        {
            return new ProducerBuilder<Null, TValue>(BrokerList, null, valueSerializer);
        }
    }

        public class ProducerBuilder<TKey> : ProducerBuilder
    {
        public ProducerBuilder(string brokerList, ISerializer<TKey> keySerializer)
            : base(brokerList)
        {
            KeySerializer = keySerializer;
        }

        public ISerializer<TKey> KeySerializer { get; }

        public ProducerBuilder<TKey, TValue> WithValueSerializer<TValue>(ISerializer<TValue> valueSerializer)
        {
            return new ProducerBuilder<TKey, TValue>(BrokerList, KeySerializer, valueSerializer);
        }
    }

    public class ProducerBuilder<TKey, TValue> : ProducerBuilder<TKey>, IProducerBuilder<TKey, TValue>
    {
        public ProducerBuilder(string brokerList, ISerializer<TKey> keySerializer, ISerializer<TValue> valueSerializer)
            : base(brokerList, keySerializer)
        {
            ValueSerializer = valueSerializer ?? throw new ArgumentNullException(nameof(valueSerializer));
        }

        public ProducerBuilder(string brokerList, ISerializer<TKey> keySerializer, ISerializer<TValue> valueSerializer, IProducerFactory<TKey, TValue> producerFactory)
            : this(brokerList, keySerializer, valueSerializer)
        {
            ProducerFactory = producerFactory ?? throw new ArgumentNullException(nameof(producerFactory));
        }

        public ISerializer<TValue> ValueSerializer { get; }
        public IProducerFactory<TKey, TValue> ProducerFactory { get; }

        public ProducerBuilder<TKey, TValue> WithProducerFactory(IProducerFactory<TKey, TValue> producerFactory)
        {
            return new ProducerBuilder<TKey, TValue>(
                BrokerList,
                KeySerializer,
                ValueSerializer,
                producerFactory
            );
        }

        public IProducer<TKey, TValue> Build()
        {
            if (ProducerFactory == null)
            {
                throw new InvalidOperationException("ProducerFactory is not set");
            }

            var config = new Dictionary<string, object>
            {
                { "bootstrap.servers", BrokerList }
            };

            var backingProducer = ProducerFactory.Create(
                config,
                KeySerializer,
                ValueSerializer);
            return new ProducerAdapter<TKey, TValue>(backingProducer);
        }
    }
}
