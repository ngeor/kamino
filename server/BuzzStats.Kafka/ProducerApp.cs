using Confluent.Kafka;
using Confluent.Kafka.Serialization;
using System;
using System.Collections.Generic;
using System.Text;
using System.Threading.Tasks;

namespace BuzzStats.Kafka
{
    public class ProducerApp : IProducer, IDisposable
    {
        private Producer<Null, string> _producer;
        public ProducerApp(string brokerList, string topic)
        {
            BrokerList = brokerList;
            Topic = topic;
        }

        public string BrokerList { get; }
        public string Topic { get; }

        public async Task Post(string message)
        {
            if (_producer == null)
            {
                var config = new Dictionary<string, object>
            {
                { "bootstrap.servers", BrokerList }
            };

                _producer = new Producer<Null, string>(
                    config,
                    null,
                    new StringSerializer(Encoding.UTF8)
                    );
            }

            Console.Write($"Posting message to topic {Topic}...");
            await _producer.ProduceAsync(
                Topic,
                null,
                message);
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
                    _producer.Dispose();
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
