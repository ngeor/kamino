using Confluent.Kafka.Serialization;
using System.Text;

namespace BuzzStats.Kafka
{
    public static class Serializers
    {
        public static StringSerializer String() => new StringSerializer(Encoding.UTF8);
        public static JsonSerializer<T> Json<T>() => new JsonSerializer<T>();
    }
}
