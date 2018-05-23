using Confluent.Kafka;
using System.Collections.Generic;

namespace BuzzStats.Kafka
{
    public class StubConsumerEvents<TKey, TValue> : IConsumerEvents<TKey, TValue>
    {
        public virtual void OnError(object sender, Error error)
        {

        }

        public virtual void OnConsumeError(object sender, Message message)
        {

        }

        public virtual void OnStatistics(object sender, string e)
        {

        }

        public virtual void OnLog(object sender, LogMessage logMessage)
        {

        }

        public virtual void OnOffsetsCommitted(object sender, CommittedOffsets offsets)
        {

        }

        public virtual void OnPartitionsRevoked(object sender, List<TopicPartition> partitions)
        {

        }

        public virtual void OnPartitionsAssigned(object sender, List<TopicPartition> partitions)
        {

        }

        public virtual void OnPartitionEOF(object sender, TopicPartitionOffset offset)
        {

        }

        public virtual void OnMessage(object sender, Message<TKey, TValue> message)
        {

        }
    }
}
