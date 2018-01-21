using Confluent.Kafka;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace BuzzStats.Kafka
{
    public class StreamingApp<TConsumerKey, TConsumerValue, TProducerKey, TProducerValue> :
        BaseStreamingApp<TConsumerKey, TConsumerValue, TProducerKey, TProducerValue>
    {
        public StreamingApp(
            string brokerList,
            ConsumerOptions<TConsumerKey, TConsumerValue> consumerOptions,
            ProducerOptions<TProducerKey, TProducerValue> producerOptions,
            Func<Message<TConsumerKey, TConsumerValue>, Task<IEnumerable<KeyValuePair<TProducerKey, TProducerValue>>>> messageConverter)
            : base(brokerList, consumerOptions, producerOptions)
        {
            MessageConverter = messageConverter ?? throw new ArgumentNullException(nameof(messageConverter));
        }

        public Func<Message<TConsumerKey, TConsumerValue>, Task<IEnumerable<KeyValuePair<TProducerKey, TProducerValue>>>> MessageConverter { get; }

        protected override Task<IEnumerable<KeyValuePair<TProducerKey, TProducerValue>>> ConvertMessage(Message<TConsumerKey, TConsumerValue> message)
        {
            return MessageConverter(message);
        }
    }
}
