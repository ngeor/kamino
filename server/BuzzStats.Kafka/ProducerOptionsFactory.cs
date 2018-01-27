using Confluent.Kafka;

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
                ValueSerializer = Serializers.String()
            };
        }

        public static ProducerOptions<Null, T> JsonValues<T>(string outputTopic)
        {
            return new ProducerOptions<Null, T>
            {
                OutputTopic = outputTopic,
                KeySerializer = null,
                ValueSerializer = Serializers.Json<T>()
            };
        }
    }
}
