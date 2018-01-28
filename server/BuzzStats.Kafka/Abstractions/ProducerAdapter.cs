using Confluent.Kafka;
using System;
using System.Threading.Tasks;

namespace BuzzStats.Kafka.Abstractions
{
    internal class ProducerAdapter<TKey, TValue> : IProducer<TKey, TValue>
    {
        private readonly Producer<TKey, TValue> producer;

        public ProducerAdapter(Producer<TKey, TValue> producer)
        {
            this.producer = producer ?? throw new ArgumentNullException(nameof(producer));
        }

        public async Task ProduceAsync(string topic, TKey key, TValue value)
        {
            await producer.ProduceAsync(topic, key, value);
        }

        #region IDisposable Support
        private bool disposedValue = false; // To detect redundant calls

        protected virtual void Dispose(bool disposing)
        {
            if (!disposedValue)
            {
                if (disposing)
                {
                    producer.Dispose();
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
