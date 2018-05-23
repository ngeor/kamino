using System;
using System.Collections.Generic;
using Confluent.Kafka;

namespace BuzzStats.Kafka
{
    public interface IConsumerEvents<TKey, TValue>
    {
        void OnConsumeError(object sender, Message message);
        void OnError(object sender, Error error);
        void OnLog(object sender, LogMessage logMessage);
        void OnMessage(object sender, Message<TKey, TValue> message);
        void OnOffsetsCommitted(object sender, CommittedOffsets offsets);
        void OnPartitionEOF(object sender, TopicPartitionOffset offset);
        void OnPartitionsAssigned(object sender, List<TopicPartition> partitions);
        void OnPartitionsRevoked(object sender, List<TopicPartition> partitions);
        void OnStatistics(object sender, string e);
    }

    public static class ConsumerEventsExtensions
    {
        public static void SubscribeConsumerEvents<TKey, TValue>(this Consumer<TKey, TValue> consumer, IConsumerEvents<TKey, TValue> consumerEvents)
        {
            consumer.OnConsumeError += consumerEvents.OnConsumeError;
            consumer.OnError += consumerEvents.OnError;
            consumer.OnLog += consumerEvents.OnLog;
            consumer.OnMessage += consumerEvents.OnMessage;
            consumer.OnOffsetsCommitted += consumerEvents.OnOffsetsCommitted;
            consumer.OnPartitionEOF += consumerEvents.OnPartitionEOF;
            consumer.OnPartitionsAssigned += consumerEvents.OnPartitionsAssigned;
            consumer.OnPartitionsRevoked += consumerEvents.OnPartitionsRevoked;
            consumer.OnStatistics += consumerEvents.OnStatistics;
        }

        public static void UnsubscribeConsumerEvents<TKey, TValue>(this Consumer<TKey, TValue> consumer, IConsumerEvents<TKey, TValue> consumerEvents)
        {
            consumer.OnConsumeError -= consumerEvents.OnConsumeError;
            consumer.OnError -= consumerEvents.OnError;
            consumer.OnLog -= consumerEvents.OnLog;
            consumer.OnMessage -= consumerEvents.OnMessage;
            consumer.OnOffsetsCommitted -= consumerEvents.OnOffsetsCommitted;
            consumer.OnPartitionEOF -= consumerEvents.OnPartitionEOF;
            consumer.OnPartitionsAssigned -= consumerEvents.OnPartitionsAssigned;
            consumer.OnPartitionsRevoked -= consumerEvents.OnPartitionsRevoked;
            consumer.OnStatistics -= consumerEvents.OnStatistics;
        }
    }

    public class ConsumerEventsDisposable<TKey, TValue> : IDisposable
    {
        private readonly Consumer<TKey, TValue> consumer;
        private readonly IConsumerEvents<TKey, TValue> consumerEvents;

        public ConsumerEventsDisposable(Consumer<TKey, TValue> consumer, IConsumerEvents<TKey, TValue> consumerEvents)
        {
            consumer.SubscribeConsumerEvents(consumerEvents);
            this.consumer = consumer;
            this.consumerEvents = consumerEvents;
        }

        public void Dispose()
        {
            consumer.UnsubscribeConsumerEvents(consumerEvents);
        }
    }
}
