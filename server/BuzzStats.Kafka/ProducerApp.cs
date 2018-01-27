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
                    // TODO: dispose managed state (managed objects).
                    _producer?.Dispose();
                }

                // TODO: free unmanaged resources (unmanaged objects) and override a finalizer below.
                // TODO: set large fields to null.

                disposedValue = true;
            }
        }

        // TODO: override a finalizer only if Dispose(bool disposing) above has code to free unmanaged resources.
        // ~ProducerApp() {
        //   // Do not change this code. Put cleanup code in Dispose(bool disposing) above.
        //   Dispose(false);
        // }

        // This code added to correctly implement the disposable pattern.
        public void Dispose()
        {
            // Do not change this code. Put cleanup code in Dispose(bool disposing) above.
            Dispose(true);
            // TODO: uncomment the following line if the finalizer is overridden above.
            // GC.SuppressFinalize(this);
        }
        #endregion
    }
}
