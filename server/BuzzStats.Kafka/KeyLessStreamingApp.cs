using Confluent.Kafka;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace BuzzStats.Kafka
{
    public class KeyLessStreamingApp<TConsumerValue, TProducerValue> : BaseStreamingApp<Null, TConsumerValue, Null, TProducerValue>
    {
        public KeyLessStreamingApp(string brokerList, ConsumerOptions<Null, TConsumerValue> consumerOptions, ProducerOptions<Null, TProducerValue> producerOptions, Func<TConsumerValue, Task<IEnumerable<TProducerValue>>> messageConverter) : base(brokerList, consumerOptions, producerOptions)
        {
            MessageConverter = messageConverter ?? throw new ArgumentNullException(nameof(messageConverter));
        }

        public Func<TConsumerValue, Task<IEnumerable<TProducerValue>>> MessageConverter { get; }

        protected override async Task<IEnumerable<KeyValuePair<Null, TProducerValue>>> ConvertMessage(Message<Null, TConsumerValue> message)
        {
            var result = await MessageConverter(message.Value);
            return result.Select(value => new KeyValuePair<Null, TProducerValue>(null, value));
        }
    }
}
