using System.Collections.Generic;
using Confluent.Kafka;
using Confluent.Kafka.Serialization;

namespace BuzzStats.Kafka.Abstractions
{
    public class ProducerFactory<TKey, TValue> : IProducerFactory<TKey, TValue>
    {
        public Producer<TKey, TValue> Create(
            IEnumerable<KeyValuePair<string, object>> config,
            ISerializer<TKey> keySerializer,
            ISerializer<TValue> valueSerializer
        )
        {
            return new Producer<TKey, TValue>(config, keySerializer, valueSerializer);
        }
    }
}
