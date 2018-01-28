using Confluent.Kafka;
using System.Threading.Tasks;

namespace BuzzStats.Kafka.Abstractions
{
    public static class ProducerExtensions
    {
        public async static Task ProduceAsync<TValue>(this IProducer<Null, TValue> producer, string topic, TValue value)
        {
            await producer.ProduceAsync(topic, null, value);
        }
    }
}
