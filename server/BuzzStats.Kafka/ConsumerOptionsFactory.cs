using Confluent.Kafka;
using Confluent.Kafka.Serialization;
using System.Text;

namespace BuzzStats.Kafka
{
    public static class ConsumerOptionsFactory
    {
        public static ConsumerOptions<Null, string> StringValues(string consumerId)
        {
            return new ConsumerOptions<Null, string>
            {
                ConsumerId = consumerId,
                KeyDeserializer = null,
                ValueDeserializer = new StringDeserializer(Encoding.UTF8)
            };
        }

        public static ConsumerOptions<Null, T> JsonValues<T>(string consumerId)
        {
            return new ConsumerOptions<Null, T>
            {
                ConsumerId = consumerId,
                KeyDeserializer = null,
                ValueDeserializer = new JsonDeserializer<T>()
            };
        }
    }
}
