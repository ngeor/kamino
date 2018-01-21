using Confluent.Kafka;
using Confluent.Kafka.Serialization;
using System.Text;

namespace BuzzStats.Kafka
{
    public static class ConsumerOptionsFactory
    {
        public static ConsumerOptions<Null, string> StringValues(string consumerId, string topic)
        {
            return new ConsumerOptions<Null, string>
            {
                ConsumerId = consumerId,
                InputTopic = topic,
                KeyDeserializer = null,
                ValueDeserializer = new StringDeserializer(Encoding.UTF8)
            };
        }
    }
}
