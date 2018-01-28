using System.Collections.Generic;
using Confluent.Kafka;
using Confluent.Kafka.Serialization;

namespace BuzzStats.Kafka.Abstractions
{
    public interface IProducerFactory<TKey, TValue>
    {
        Producer<TKey, TValue> Create(
            IEnumerable<KeyValuePair<string, object>> config,
            ISerializer<TKey> keySerializer,
            ISerializer<TValue> valueSerializer);
    }
}
