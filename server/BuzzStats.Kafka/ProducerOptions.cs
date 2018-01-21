using Confluent.Kafka.Serialization;

namespace BuzzStats.Kafka
{
    public class ProducerOptions<TKey, TValue>
    {
        public string OutputTopic { get; set; }
        public ISerializer<TKey> KeySerializer { get; set; }
        public ISerializer<TValue> ValueSerializer { get; set; }
    }
}
