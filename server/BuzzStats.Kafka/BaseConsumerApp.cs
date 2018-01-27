using Confluent.Kafka;
using log4net;
using System;
using System.Collections.Generic;
using System.Reflection;

namespace BuzzStats.Kafka
{
    public abstract class BaseConsumerApp
    {
        protected static readonly ILog Log = LogManager.GetLogger(
            Assembly.GetEntryAssembly(), typeof(BaseConsumerApp));

        protected BaseConsumerApp()
        {
        }
    }

    public class BaseConsumerApp<TKey, TValue> : BaseConsumerApp
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
            Log.Info($"Topic: {msg.Topic} Partition: {msg.Partition} Offset: {msg.Offset} {msg.Value}");
        }

        public bool IsCancelled { get; set; }
        public bool HandleCancelKeyPress { get; set; } = true;

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
                    => Log.Info($"Reached end of topic {end.Topic} partition {end.Partition}, next message will be at offset {end.Offset}");

                // Raised on critical errors, e.g. connection failures or all brokers down.
                consumer.OnError += (_, error)
                    => Log.Error($"Error: {error}");

                // Raised on deserialization errors or when a consumed message has an error != NoError.
                consumer.OnConsumeError += (_, msg)
                    => Log.Error($"Error consuming from topic/partition/offset {msg.Topic}/{msg.Partition}/{msg.Offset}: {msg.Error}");

                consumer.OnOffsetsCommitted += (_, commit) =>
                {
                    Log.Info($"[{string.Join(", ", commit.Offsets)}]");

                    if (commit.Error)
                    {
                        Log.Error($"Failed to commit offsets: {commit.Error}");
                    }

                    Log.Info($"Successfully committed offsets: [{string.Join(", ", commit.Offsets)}]");
                };

                consumer.OnPartitionsAssigned += (_, partitions) =>
                {
                    Log.Info($"Assigned partitions: [{string.Join(", ", partitions)}], member id: {consumer.MemberId}");
                    consumer.Assign(partitions);
                };

                consumer.OnPartitionsRevoked += (_, partitions) =>
                {
                    Log.Info($"Revoked partitions: [{string.Join(", ", partitions)}]");
                    consumer.Unassign();
                };

                consumer.OnStatistics += (_, json)
                    => Log.Debug($"Statistics: {json}");

                consumer.Subscribe(ConsumerOptions.InputTopic);

                Log.Info($"Subscribed to: [{string.Join(", ", consumer.Subscription)}]");

                if (HandleCancelKeyPress)
                {
                    Console.CancelKeyPress += (_, e) =>
                    {
                        e.Cancel = true; // prevent the process from terminating.
                        IsCancelled = true;
                    };

                    Console.WriteLine("Ctrl-C to exit.");
                }

                Log.Info("Polling started");

                while (!IsCancelled)
                {
                    consumer.Poll(TimeSpan.FromMilliseconds(100));
                }

                Log.Info("Polling stopped");
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
