using Confluent.Kafka;
using Confluent.Kafka.Serialization;
using System.Text;

namespace BuzzStats.Kafka
{
    public static class ProducerOptionsFactory
    {
        public static ProducerOptions<Null, string> StringValues(string outputTopic)
        {
            return new ProducerOptions<Null, string>
            {
                OutputTopic = outputTopic,
                KeySerializer = null,
                ValueSerializer = new StringSerializer(Encoding.UTF8)
            };
        }
    }
}
