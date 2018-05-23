using Confluent.Kafka;
using Confluent.Kafka.Serialization;
using System;
using System.Collections.Generic;
using System.Threading;
using System.Threading.Tasks;

namespace BuzzStats.Kafka
{
    public class ProducerObserver : IObserver<byte[]>, IDisposable
    {
        private Producer<Null, byte[]> producer;

        public ProducerObserver(string brokerList, string topic)
        {
            var config = new Dictionary<string, object>
            {
                { "bootstrap.servers", brokerList }
            };

            producer = new Producer<Null, byte[]>(config, new NullSerializer(), new ByteArraySerializer());
            Topic = topic;
        }

        public string Topic { get; }

        public void Dispose()
        {
            Interlocked.Exchange(ref producer, null)?.Dispose();
        }

        public void OnCompleted()
        {
            Dispose();
        }

        public void OnError(Exception error)
        {
            throw error;
        }

        public void OnNext(byte[] value)
        {
            Task.Run(async () =>
            {
                await producer.ProduceAsync(Topic, null, value);
            }).GetAwaiter().GetResult();
        }       
    }

    public class ProducerBuilder<TKey, TValue>
    {
        public ProducerBuilder(string brokerList, ISerializer<TKey> keySerializer, ISerializer<TValue> valueSerializer)
        {
            BrokerList = brokerList ?? throw new ArgumentNullException(nameof(brokerList));
            KeySerializer = keySerializer;
            ValueSerializer = valueSerializer ?? throw new ArgumentNullException(nameof(valueSerializer));
        }

        public string BrokerList { get; }
        public ISerializer<TKey> KeySerializer { get; }
        public ISerializer<TValue> ValueSerializer { get; }

        public Producer<TKey, TValue> Build()
        {
            var config = new Dictionary<string, object>
            {
                { "bootstrap.servers", BrokerList }
            };

            return new Producer<TKey, TValue>(
                config,
                KeySerializer,
                ValueSerializer);
        }
    }
}
