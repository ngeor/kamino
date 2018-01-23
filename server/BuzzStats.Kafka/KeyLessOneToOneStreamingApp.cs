using Confluent.Kafka;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace BuzzStats.Kafka
{
    public class KeyLessOneToOneStreamingApp<TConsumerValue, TProducerValue> : BaseStreamingApp<Null, TConsumerValue, Null, TProducerValue>
    {
        public KeyLessOneToOneStreamingApp(
            string brokerList,
            ConsumerOptions<Null, TConsumerValue> consumerOptions,
            ProducerOptions<Null, TProducerValue> producerOptions,
            Func<TConsumerValue, Task<TProducerValue>> messageConverter)
            : base(brokerList, consumerOptions, producerOptions)
        {
            MessageConverter = messageConverter ?? throw new ArgumentNullException(nameof(messageConverter));
        }

        public Func<TConsumerValue, Task<TProducerValue>> MessageConverter { get; }

        protected override async Task<IEnumerable<KeyValuePair<Null, TProducerValue>>> ConvertMessage(Message<Null, TConsumerValue> message)
        {
            var result = await MessageConverter(message.Value);
            return Enumerable.Repeat(
                new KeyValuePair<Null, TProducerValue>(null, result),
                1);
        }
    }
}
