using Confluent.Kafka;
using Microsoft.Extensions.Logging;
using System.Collections.Generic;

namespace BuzzStats.Kafka
{
    public class LogConsumerEvents<TKey, TValue> : StubConsumerEvents<TKey, TValue>
    {
        public LogConsumerEvents(ILogger log)
        {
            Log = log;
        }

        public ILogger Log { get; }

        public override void OnPartitionEOF(object sender, TopicPartitionOffset end)
        {
            Log.LogDebug($"Reached end of topic {end.Topic} partition {end.Partition}, next message will be at offset {end.Offset}");
        }

        public override void OnError(object sender, Error error)
        {
            Log.LogError($"Error: {error}");
        }

        public override void OnConsumeError(object sender, Message msg)
        {
            Log.LogError($"Error consuming from topic/partition/offset {msg.Topic}/{msg.Partition}/{msg.Offset}: {msg.Error}");
        }

        public override void OnOffsetsCommitted(object sender, CommittedOffsets commit)
        {
            Log.LogDebug($"[{string.Join(", ", commit.Offsets)}]");

            if (commit.Error)
            {
                Log.LogError($"Failed to commit offsets: {commit.Error}");
            }

            Log.LogDebug($"Successfully committed offsets: [{string.Join(", ", commit.Offsets)}]");
        }

        public override void OnPartitionsAssigned(object sender, List<TopicPartition> partitions)
        {
            Consumer consumer = sender as Consumer;
            string memberId = consumer != null ? consumer.MemberId : "N/A";
            Log.LogInformation($"Assigned partitions: [{string.Join(", ", partitions)}], member id: {memberId}");
        }

        public override void OnPartitionsRevoked(object sender, List<TopicPartition> partitions)
        {
            Log.LogInformation($"Revoked partitions: [{string.Join(", ", partitions)}]");
        }

        public override void OnStatistics(object sender, string json)
        {
            Log.LogDebug($"Statistics: {json}");
        }
    }
}
