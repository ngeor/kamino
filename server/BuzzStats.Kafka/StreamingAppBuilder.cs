using Confluent.Kafka;
using Confluent.Kafka.Serialization;
using System;
using System.Text;

namespace BuzzStats.Kafka
{
    public static class StreamingAppBuilder
    {
        public static StreamingAppBuilder<TConsumerValue, TProducerValue> JsonToJson<TConsumerValue, TProducerValue>()
        {
            return new StreamingAppBuilder<TConsumerValue, TProducerValue>
            {
                ConsumerValueDeserializer = new JsonDeserializer<TConsumerValue>(),
                ProducerValueSerializer = Serializers.Json<TProducerValue>()
            };
        }

        public static StreamingAppBuilder<string, TProducerValue> StringToJson<TProducerValue>()
        {
            return new StreamingAppBuilder<string, TProducerValue>
            {
                ConsumerValueDeserializer = new StringDeserializer(Encoding.UTF8),
                ProducerValueSerializer = Serializers.Json<TProducerValue>()
            };
        }
    }

    public class StreamingAppBuilder<TConsumerValue, TProducerValue>
    {
        public string BrokerList { get; set; }
        public string ConsumerId { get; set; }
        public IDeserializer<TConsumerValue> ConsumerValueDeserializer { get; set; }
        public ISerializer<TProducerValue> ProducerValueSerializer { get; set; }

        public StreamingAppBuilder<TConsumerValue, TProducerValue> WithBrokerList(string brokerList)
        {
            BrokerList = brokerList;
            return this;
        }

        public StreamingAppBuilder<TConsumerValue, TProducerValue> WithConsumerId(string consumerId)
        {
            ConsumerId = consumerId;
            return this;
        }

        public void Run(
            Action<IConsumerApp<Null, TConsumerValue>, ISerializingProducer<Null, TProducerValue>> action)
        {
            var consumer = new ConsumerApp<Null, TConsumerValue>(
                BrokerList,
                ConsumerId,
                null,
                ConsumerValueDeserializer);

            using (var producer = new ProducerBuilder<Null, TProducerValue>(
                BrokerList, null, ProducerValueSerializer).Build())
            {
                action(consumer, producer);
            }
        }
    }
}
