using System;
using System.Collections.Generic;
using System.Text;
using System.Threading.Tasks;
using Confluent.Kafka.Serialization;

namespace BuzzStats.Kafka.Abstractions
{
    public interface IProducer<TKey, TValue> : IDisposable
    {
        Task ProduceAsync(string topic, TKey key, TValue value);
    }
}
