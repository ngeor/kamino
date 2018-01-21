using Confluent.Kafka;
using Confluent.Kafka.Serialization;
using System;
using System.Collections.Generic;
using System.Text;

namespace BuzzStats.Kafka
{
    public class BaseConsumerApp<TKey, TValue>
    {
        public BaseConsumerApp(
            string brokerList,
            ConsumerOptions<TKey, TValue> consumerOptions)
        {
            BrokerList = brokerList ?? throw new ArgumentNullException(nameof(brokerList));
            ConsumerOptions = consumerOptions ?? throw new ArgumentNullException(nameof(consumerOptions));
        }

        public string BrokerList { get; }
        public ConsumerOptions<TKey, TValue> ConsumerOptions { get; }

        protected virtual void OnMessage(Message<TKey, TValue> msg)
        {
            Console.WriteLine($"Topic: {msg.Topic} Partition: {msg.Partition} Offset: {msg.Offset} {msg.Value}");
        }

        public void Poll()
        {
            using (var consumer = new Consumer<TKey, TValue>(
                ConstructConfig(true),
                ConsumerOptions.KeyDeserializer,
                ConsumerOptions.ValueDeserializer))
            {
                // Note: All event handlers are called on the main thread.

                consumer.OnMessage += (_, msg)
                    =>
                {
                    OnMessage(msg);
                };

                consumer.OnPartitionEOF += (_, end)
                    => Console.WriteLine($"Reached end of topic {end.Topic} partition {end.Partition}, next message will be at offset {end.Offset}");

                // Raised on critical errors, e.g. connection failures or all brokers down.
                consumer.OnError += (_, error)
                    => Console.WriteLine($"Error: {error}");

                // Raised on deserialization errors or when a consumed message has an error != NoError.
                consumer.OnConsumeError += (_, msg)
                    => Console.WriteLine($"Error consuming from topic/partition/offset {msg.Topic}/{msg.Partition}/{msg.Offset}: {msg.Error}");

                consumer.OnOffsetsCommitted += (_, commit) =>
                {
                    Console.WriteLine($"[{string.Join(", ", commit.Offsets)}]");

                    if (commit.Error)
                    {
                        Console.WriteLine($"Failed to commit offsets: {commit.Error}");
                    }
                    Console.WriteLine($"Successfully committed offsets: [{string.Join(", ", commit.Offsets)}]");
                };

                consumer.OnPartitionsAssigned += (_, partitions) =>
                {
                    Console.WriteLine($"Assigned partitions: [{string.Join(", ", partitions)}], member id: {consumer.MemberId}");
                    consumer.Assign(partitions);
                };

                consumer.OnPartitionsRevoked += (_, partitions) =>
                {
                    Console.WriteLine($"Revoked partitions: [{string.Join(", ", partitions)}]");
                    consumer.Unassign();
                };

                consumer.OnStatistics += (_, json)
                    => Console.WriteLine($"Statistics: {json}");

                consumer.Subscribe(ConsumerOptions.InputTopic);

                Console.WriteLine($"Subscribed to: [{string.Join(", ", consumer.Subscription)}]");

                var cancelled = false;
                Console.CancelKeyPress += (_, e) =>
                {
                    e.Cancel = true; // prevent the process from terminating.
                    cancelled = true;
                };

                Console.WriteLine("Ctrl-C to exit.");
                while (!cancelled)
                {
                    consumer.Poll(TimeSpan.FromMilliseconds(100));
                }
            }
        }

        private Dictionary<string, object> ConstructConfig(bool enableAutoCommit) =>
            new Dictionary<string, object>
            {
                { "group.id", ConsumerOptions.ConsumerId },
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
