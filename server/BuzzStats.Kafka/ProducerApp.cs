using Confluent.Kafka;
using System;
using System.Threading.Tasks;

namespace BuzzStats.Kafka
{
    public class ProducerApp<TKey, TValue> : IProducer<TKey, TValue>, IDisposable
    {
        private readonly Producer<TKey, TValue> _producer;

        public ProducerApp(string brokerList, ProducerOptions<TKey, TValue> producerOptions)
        {
            ProducerOptions = producerOptions ?? throw new ArgumentNullException(nameof(producerOptions));

            _producer = new ProducerBuilder<TKey, TValue>(brokerList, producerOptions.KeySerializer, producerOptions.ValueSerializer)
                .Build();
        }

        private ProducerOptions<TKey, TValue> ProducerOptions { get; }

        public async Task Post(TKey key, TValue value)
        {
            await _producer.ProduceAsync(
                ProducerOptions.OutputTopic,
                key,
                value);
        }

        #region IDisposable Support
        private bool disposedValue = false; // To detect redundant calls

        protected virtual void Dispose(bool disposing)
        {
            if (!disposedValue)
            {
                if (disposing)
                {
                    _producer.Dispose();
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
