using Confluent.Kafka.Serialization;
using Newtonsoft.Json;
using System.Collections.Generic;
using System.Text;

namespace BuzzStats.Kafka
{
    public class JsonDeserializer<T> : IDeserializer<T>
    {
        public IEnumerable<KeyValuePair<string, object>> Configure(IEnumerable<KeyValuePair<string, object>> config, bool isKey)
        {
            return config;
        }

        public T Deserialize(string topic, byte[] data)
        {
            return JsonConvert.DeserializeObject<T>(Encoding.UTF8.GetString(data));
        }
    }
}
