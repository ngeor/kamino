using Confluent.Kafka.Serialization;

namespace BuzzStats.Kafka
{
    public class ConsumerOptions<TKey, TValue>
    {
        public string ConsumerId { get; set; }
        public IDeserializer<TKey> KeyDeserializer { get; set; }
        public IDeserializer<TValue> ValueDeserializer { get; set; }
    }
}
