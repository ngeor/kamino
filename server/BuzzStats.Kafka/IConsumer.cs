using Confluent.Kafka;
using System;

namespace BuzzStats.Kafka
{
    public interface IConsumer<TKey, TValue>
    {
        event EventHandler<Message<TKey, TValue>> MessageReceived;
    }
}
