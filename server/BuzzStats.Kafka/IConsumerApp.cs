using Confluent.Kafka;
using System;

namespace BuzzStats.Kafka
{
    public interface IConsumerApp<TKey, TValue>
    {
        event EventHandler<Message<TKey, TValue>> MessageReceived;
        void Poll(string topic);
        bool IsCancelled { get; set; }
    }
}
