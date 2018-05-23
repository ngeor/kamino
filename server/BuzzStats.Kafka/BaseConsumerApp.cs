using Confluent.Kafka;
using Confluent.Kafka.Serialization;
using System;
using System.Collections.Generic;
using System.Threading;

namespace BuzzStats.Kafka
{
    public class ConsumerOptions
    {
        public string ConsumerId { get; set; }
        public string BrokerList { get; set; }
        public string Topic { get; set; }
        public bool EnableAutoCommit { get; set; } = true;
        public TimeSpan PollInterval { get; set; } = TimeSpan.FromMilliseconds(100);
    }

    public class ConsumerObservable : IObservable<byte[]>, IDisposable
    {
        private readonly List<IObserver<byte[]>> observers = new List<IObserver<byte[]>>();
        private readonly ConsumerEvents consumerEvents;

        public ConsumerObservable(ConsumerOptions consumerOptions)
        {
            consumerEvents = new ConsumerEvents(this);

            Consumer = new Consumer<Ignore, byte[]>(
                ConstructConfig(consumerOptions),
                new IgnoreDeserializer(),
                new ByteArrayDeserializer());

            Consumer.SubscribeConsumerEvents(consumerEvents);
            Consumer.Subscribe(consumerOptions.Topic);
            ConsumerOptions = consumerOptions;
        }

        public void Dispose()
        {
            if (Consumer != null)
            {
                Consumer.Dispose();
                Consumer.UnsubscribeConsumerEvents(consumerEvents);
                Consumer = null;
            }
        }

        private Dictionary<string, object> ConstructConfig(ConsumerOptions consumerOptions) =>
            new Dictionary<string, object>
            {
                { "group.id", consumerOptions.ConsumerId },
                { "enable.auto.commit", consumerOptions.EnableAutoCommit },
                { "auto.commit.interval.ms", 5000 },
                { "statistics.interval.ms", 60000 },
                { "bootstrap.servers", consumerOptions.BrokerList ?? "127.0.0.1" },
                { "default.topic.config", new Dictionary<string, object>()
                    {
                        { "auto.offset.reset", "smallest" }
                    }
                }
            };

        private void Consumer_OnMessage(object sender, Message<Ignore, byte[]> e)
        {
            OnNext(e.Value);
        }

        public void SubscribeConsumerEvents(IConsumerEvents<Ignore, byte[]> logConsumerEvents)
        {
            Consumer.SubscribeConsumerEvents(logConsumerEvents);
        }

        private Consumer<Ignore, byte[]> Consumer { get; set; }
        public ConsumerOptions ConsumerOptions { get; }

        public IDisposable Subscribe(IObserver<byte[]> observer)
        {
            lock (observers)
            {
                observers.Add(observer);
                return new Subscription(this, observer);
            }
        }

        void Unsubscribe(IObserver<byte[]> observer)
        {
            lock (observers)
            {
                observers.Remove(observer);
            }
        }

        private void OnNext(byte[] message)
        {
            foreach (var observer in SafeObservers())
            {
                observer.OnNext(message);
            }
        }

        public void Poll(CancellationToken token)
        {
            while (!token.IsCancellationRequested)
            {
                Consumer.Poll(ConsumerOptions.PollInterval);
            }

            foreach (var observer in SafeObservers())
            {
                observer.OnCompleted();
            }
        }

        private void OnError(Exception exception)
        {
            foreach (var observer in SafeObservers())
            {
                observer.OnError(exception);
            }
        }

        private IEnumerable<IObserver<byte[]>> SafeObservers()
        {
            lock (observers)
            {
                return observers.ToArray();
            }
        }

        class ConsumerEvents : StubConsumerEvents<Ignore, byte[]>
        {
            private readonly ConsumerObservable consumerObservable;

            public ConsumerEvents(ConsumerObservable consumerObservable)
            {
                this.consumerObservable = consumerObservable ?? throw new ArgumentNullException(nameof(consumerObservable));
            }

            public override void OnMessage(object sender, Message<Ignore, byte[]> message)
            {
                consumerObservable.OnNext(message.Value);
            }

            public override void OnError(object sender, Error error)
            {
                consumerObservable.OnError(new ConsumerErrorException(error));
            }

            public override void OnConsumeError(object sender, Message message)
            {
                consumerObservable.OnError(new ConsumerMessageException(message));
            }

            public override void OnPartitionsAssigned(object sender, List<TopicPartition> partitions)
            {
                consumerObservable.Consumer.Assign(partitions);
            }

            public override void OnPartitionsRevoked(object sender, List<TopicPartition> partitions)
            {
                consumerObservable.Consumer.Unassign();
            }
        }

        class Subscription : IDisposable
        {
            private ConsumerObservable consumerObservable;
            private IObserver<byte[]> observer;

            public Subscription(ConsumerObservable consumerObservable, IObserver<byte[]> observer)
            {
                this.consumerObservable = consumerObservable;
                this.observer = observer;
            }

            public void Dispose()
            {
                consumerObservable?.Unsubscribe(observer);
                consumerObservable = null;
                observer = null;
            }
        }
    }

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
