using Confluent.Kafka;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace BuzzStats.Kafka
{
    public abstract class BaseStreamingApp<TConsumerKey, TConsumerValue, TProducerKey, TProducerValue> : BaseConsumerApp<TConsumerKey, TConsumerValue>
    {
        protected BaseStreamingApp(
            string brokerList,
            ConsumerOptions<TConsumerKey, TConsumerValue> consumerOptions,
            ProducerOptions<TProducerKey, TProducerValue> producerOptions)
            : base(brokerList, consumerOptions)
        {
            ProducerOptions = producerOptions ?? throw new ArgumentNullException(nameof(producerOptions));
        }

        public ProducerOptions<TProducerKey, TProducerValue> ProducerOptions { get; }

        protected override void OnMessage(Message<TConsumerKey, TConsumerValue> msg)
        {
            base.OnMessage(msg);
            Task.Run(async () =>
            {
                using (var producer = new ProducerApp<TProducerKey, TProducerValue>(BrokerList, ProducerOptions))
                {
                    foreach (var outputMessage in await ConvertMessage(msg))
                    {
                        await producer.Post(outputMessage.Key, outputMessage.Value);
                    }
                }
            }).GetAwaiter().GetResult();
        }

        protected abstract Task<IEnumerable<KeyValuePair<TProducerKey, TProducerValue>>> ConvertMessage(Message<TConsumerKey, TConsumerValue> message);
    }
}
