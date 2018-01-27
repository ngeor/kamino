using Confluent.Kafka;
using Confluent.Kafka.Serialization;
using System;
using System.Collections.Generic;

namespace BuzzStats.Kafka
{
    public class ProducerBuilder<TKey, TValue>
    {
        public ProducerBuilder(string brokerList, ISerializer<TKey> keySerializer, ISerializer<TValue> valueSerializer)
        {
            BrokerList = brokerList ?? throw new ArgumentNullException(nameof(brokerList));
            KeySerializer = keySerializer;
            ValueSerializer = valueSerializer ?? throw new ArgumentNullException(nameof(valueSerializer));
        }

        public string BrokerList { get; }
        public ISerializer<TKey> KeySerializer { get; }
        public ISerializer<TValue> ValueSerializer { get; }

        public Producer<TKey, TValue> Build()
        {
            var config = new Dictionary<string, object>
            {
                { "bootstrap.servers", BrokerList }
            };

            return new Producer<TKey, TValue>(
                config,
                KeySerializer,
                ValueSerializer);
        }
    }
}
