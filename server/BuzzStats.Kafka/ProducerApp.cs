using Confluent.Kafka;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace BuzzStats.Kafka
{
    public class ProducerApp<TKey, TValue> : IProducer<TKey, TValue>, IDisposable
    {
        private Producer<TKey, TValue> _producer;
        public ProducerApp(string brokerList, ProducerOptions<TKey, TValue> producerOptions)
        {
            BrokerList = brokerList ?? throw new ArgumentNullException(nameof(brokerList));
            ProducerOptions = producerOptions ?? throw new ArgumentNullException(nameof(producerOptions));
        }

        public string BrokerList { get; }
        public ProducerOptions<TKey, TValue> ProducerOptions { get; }

        public async Task Post(TKey key, TValue value)
        {
            if (_producer == null)
            {
                var config = new Dictionary<string, object>
            {
                { "bootstrap.servers", BrokerList }
            };

                _producer = new Producer<TKey, TValue>(
                    config,
                    ProducerOptions.KeySerializer,
                    ProducerOptions.ValueSerializer);
            }

            Console.Write($"Posting message to topic {ProducerOptions.OutputTopic}...");
            await _producer.ProduceAsync(
                ProducerOptions.OutputTopic,
                key,
                value);
            Console.WriteLine(" done");
        }

        #region IDisposable Support
        private bool disposedValue = false; // To detect redundant calls

        protected virtual void Dispose(bool disposing)
        {
            if (!disposedValue)
            {
                if (disposing)
                {
                    _producer?.Dispose();
                }

                disposedValue = true;
            }
        }

        // This code added to correctly implement the disposable pattern.
        public void Dispose()
        {
            // Do not change this code. Put cleanup code in Dispose(bool disposing) above.
            Dispose(true);
            GC.SuppressFinalize(this);
        }
        #endregion
    }
}
