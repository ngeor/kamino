using Confluent.Kafka;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace BuzzStats.Kafka
{
    public class StreamingApp : BaseConsumerApp
    {
        public StreamingApp(string brokerList, string consumerId, string inputTopic, string outputTopic, Func<string, Task<IEnumerable<string>>> messageConverter)
            : base(brokerList, consumerId, inputTopic)
        {
            OutputTopic = outputTopic;
            MessageConverter = messageConverter;
        }

        public string OutputTopic { get; }
        public Func<string, Task<IEnumerable<string>>> MessageConverter { get; }

        protected override void OnMessage(Message<Null, string> msg)
        {
            base.OnMessage(msg);
            Task.Run(async () =>
            {
                using (var producer = new ProducerApp(BrokerList, OutputTopic))
                {
                    foreach (var outputMessage in await MessageConverter(msg.Value))
                    {
                        await producer.Post(outputMessage);
                    }
                }
            }).GetAwaiter().GetResult();
        }
    }
}
