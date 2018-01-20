using Confluent.Kafka;
using System;

namespace BuzzStats.Kafka
{
    public class ConsumerApp : BaseConsumerApp, IConsumer
    {
        public ConsumerApp(string brokerList, string consumerId, string topic)
            : base(brokerList, consumerId, topic)
        {
        }

        public event EventHandler<string> MessageReceived;

        protected override void OnMessage(Message<Null, string> msg)
        {
            base.OnMessage(msg);
            MessageReceived?.Invoke(this, msg.Value);
        }
    }
}
