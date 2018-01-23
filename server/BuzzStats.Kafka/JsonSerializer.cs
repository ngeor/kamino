using Confluent.Kafka.Serialization;
using Newtonsoft.Json;
using System.Collections.Generic;
using System.Text;

namespace BuzzStats.Kafka
{
    public class JsonSerializer<T> : ISerializer<T>
    {
        public IEnumerable<KeyValuePair<string, object>> Configure(IEnumerable<KeyValuePair<string, object>> config, bool isKey)
        {
            return config;
        }

        public byte[] Serialize(string topic, T data)
        {
            return Encoding.UTF8.GetBytes(JsonConvert.ToString(data));
        }
    }
}
