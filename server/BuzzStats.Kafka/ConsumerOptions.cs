using System;

namespace BuzzStats.Kafka
{
    public class ConsumerOptions
    {
        public string ConsumerId { get; set; }
        public string BrokerList { get; set; }
        public string Topic { get; set; }
        public TimeSpan PollInterval { get; set; } = TimeSpan.FromMilliseconds(100);
    }
}
