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

        public event EventHandler<Message<TKey, TValue>> MessageReceived;

        protected override void OnMessage(Message<TKey, TValue> msg)
        {
            base.OnMessage(msg);
            MessageReceived?.Invoke(this, msg);
        }
    }
}
