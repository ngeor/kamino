using Confluent.Kafka;
using Confluent.Kafka.Serialization;
using System;
using System.Collections.Generic;

namespace BuzzStats.Kafka
{
    public abstract class BaseConsumerApp
    {
        protected BaseConsumerApp()
        {
        }
    }

    public class BaseConsumerApp<TKey, TValue> : BaseConsumerApp, IConsumerApp<TKey, TValue>
    {
        public BaseConsumerApp(
            string brokerList,
            string consumerId,
            IDeserializer<TKey> keyDeserializer,
            IDeserializer<TValue> valueDeserializer)
        {
            BrokerList = brokerList ?? throw new ArgumentNullException(nameof(brokerList));
            ConsumerId = consumerId;
            KeyDeserializer = keyDeserializer;
            ValueDeserializer = valueDeserializer;
        }

        public event EventHandler<Message<TKey, TValue>> MessageReceived;

        public string BrokerList { get; }
        public string ConsumerId { get; }
        public IDeserializer<TKey> KeyDeserializer { get; }
        public IDeserializer<TValue> ValueDeserializer { get; }

        protected virtual void OnMessage(Message<TKey, TValue> msg)
        {
            MessageReceived?.Invoke(this, msg);
        }

        public bool IsCancelled { get; set; }
        public bool HandleCancelKeyPress { get; set; } = true;

        public void Poll(string topic)
        {
            using (var consumer = new Consumer<TKey, TValue>(
                ConstructConfig(true),
                KeyDeserializer,
                ValueDeserializer))
            {
                // Note: All event handlers are called on the main thread.

                consumer.OnMessage += (_, msg)
                    =>
                {
                    OnMessage(msg);
                };

                consumer.OnPartitionsAssigned += (_, partitions) =>
                {
                    consumer.Assign(partitions);
                };

                consumer.OnPartitionsRevoked += (_, partitions) =>
                {
                    consumer.Unassign();
                };

                consumer.Subscribe(topic);

                if (HandleCancelKeyPress)
                {
                    Console.CancelKeyPress += (_, e) =>
                    {
                        e.Cancel = true; // prevent the process from terminating.
                        IsCancelled = true;
                    };

                    Console.WriteLine("Ctrl-C to exit.");
                }

                while (!IsCancelled)
                {
                    consumer.Poll(TimeSpan.FromMilliseconds(100));
                }
            }
        }

        private Dictionary<string, object> ConstructConfig(bool enableAutoCommit) =>
            new Dictionary<string, object>
            {
                { "group.id", ConsumerId },
                { "enable.auto.commit", enableAutoCommit },
                { "auto.commit.interval.ms", 5000 },
                { "statistics.interval.ms", 60000 },
                { "bootstrap.servers", BrokerList },
                { "default.topic.config", new Dictionary<string, object>()
                    {
                        { "auto.offset.reset", "smallest" }
                    }
                }
            };
    }
}
