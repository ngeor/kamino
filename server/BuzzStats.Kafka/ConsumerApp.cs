using Confluent.Kafka;
using System;

namespace BuzzStats.Kafka
{
    public class ConsumerApp<TKey, TValue> : BaseConsumerApp<TKey, TValue>, IConsumer<TKey, TValue>
    {
        public ConsumerApp(string brokerList, ConsumerOptions<TKey, TValue> consumerOptions)
            : base(brokerList, consumerOptions)
        {
        }
    }
}
