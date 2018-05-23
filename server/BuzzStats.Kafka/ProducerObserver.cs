using Confluent.Kafka;
using Confluent.Kafka.Serialization;
using Microsoft.Extensions.Logging;
using System;
using System.Collections.Generic;
using System.Threading;
using System.Threading.Tasks;

namespace BuzzStats.Kafka
{
    public class ProducerObserver : IDisposable
    {
        private readonly ILogger logger;
        private Producer<Null, byte[]> producer;

        public ProducerObserver(string brokerList, string topic, ILogger logger)
        {
            var config = new Dictionary<string, object>
            {
                { "bootstrap.servers", brokerList }
            };

            producer = new Producer<Null, byte[]>(config, new NullSerializer(), new ByteArraySerializer());
            Topic = topic;
            this.logger = logger;
        }

        public string Topic { get; }

        public void Dispose()
        {
            Interlocked.Exchange(ref producer, null)?.Dispose();
        }

        public async Task<Message<Null, byte[]>> ProduceAsync(byte[] value)
        {
            logger.LogInformation("Publishing message to topic {0}", Topic);
            return await producer.ProduceAsync(Topic, null, value);
        }
    }
}
