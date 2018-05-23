using Confluent.Kafka;
using Confluent.Kafka.Serialization;
using System;
using System.Collections.Generic;
using System.Reactive.Linq;
using System.Threading;
using System.Threading.Tasks;
using System.Reactive.Threading.Tasks;

namespace BuzzStats.Kafka
{
    public static class ObservableExtensions
    {
        public static IObservable<T2> Merge<T1, T2>(
            this IObservable<T1> observable,
            Func<T1, IObservable<T2>> func
        )
        {
            return observable.Select(func).Merge();
        }

        public static IObservable<Tuple<T1, T2>> PackPayload<T1, T2>(
            this IObservable<T1> observable,
            Func<T1, IObservable<T2>> func)
        {
            var q =
                from message in observable
                let newObservable = func(message)
                select newObservable.Select(x => Tuple.Create(message, x));
            return q.Merge();
        }

        public static IObservable<Tuple<T, T2>> RepackPayload<T, T1, T2>(
            this IObservable<Tuple<T, T1>> observable,
            Func<T1, IObservable<T2>> func)
        {
            var q =
                from t in observable
                let message = t.Item1
                let value = t.Item2
                let newObservable = func(value)
                select newObservable.Select(x => Tuple.Create(message, x));
            return q.Merge();
        }

        public static IObservable<Tuple<Message<Ignore, byte[]>, T2>> RepackPayloadTask<T1, T2>(
            this IObservable<Tuple<Message<Ignore, byte[]>, T1>> observable,
            Func<T1, Task<T2>> func)
        {
            var q =
                from t in observable
                let message = t.Item1
                let value = t.Item2
                let newObservable = func(value).ToObservable()
                select newObservable.Select(x => Tuple.Create(message, x));
            return q.Merge();
        }
    }

    abstract class ObservableBridge<TIn, TOut> : IObservable<TOut>, IDisposable
    {
        private readonly IObservable<TIn> backingObservable;
        private readonly ObserverList<TOut> observers = new ObserverList<TOut>();
        private IDisposable subscription;

        public ObservableBridge(IObservable<TIn> backingObservable)
        {
            this.backingObservable = backingObservable;
            this.subscription = backingObservable.Subscribe(OnNext, OnError, OnCompleted);
        }

        public void Dispose()
        {
            Interlocked.Exchange(ref subscription, null)?.Dispose();
        }

        public IDisposable Subscribe(IObserver<TOut> observer)
        {
            return observers.Add(observer);
        }

        protected abstract TOut Convert(TIn value);

        private void OnNext(TIn next)
        {
            TOut convertedValue = Convert(next);
            foreach (var observer in observers.ToEnumerable())
            {
                observer.OnNext(convertedValue);
            }
        }

        private void OnError(Exception exception)
        {
            foreach (var observer in observers.ToEnumerable())
            {
                observer.OnError(exception);
            }
        }

        private void OnCompleted()
        {
            foreach (var observer in observers.ToEnumerable())
            {
                observer.OnCompleted();
            }
        }
    }

    class ConsumerSubject : ObservableBridge<Message<Ignore, byte[]>, Message<Ignore, byte[]>>
    {
        private readonly ConsumerObservable consumerObservable;

        public ConsumerSubject(ConsumerObservable consumerObservable)
            : base(consumerObservable)
        {
            this.consumerObservable = consumerObservable;
        }

        protected override Message<Ignore, byte[]> Convert(Message<Ignore, byte[]> value)
        {
            return value;
        }
    }

    // TODO: use IConnectableObservable here
    public class ConsumerObservable : IObservable<Message<Ignore, byte[]>>, IDisposable
    {
        private readonly ObserverList<Message<Ignore, byte[]>> observers = new ObserverList<Message<Ignore, byte[]>>();
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
                { "enable.auto.commit", false },
                { "auto.commit.interval.ms", 5000 },
                { "statistics.interval.ms", 60000 },
                { "bootstrap.servers", consumerOptions.BrokerList ?? "127.0.0.1" },
                { "default.topic.config", new Dictionary<string, object>()
                    {
                        { "auto.offset.reset", "smallest" }
                    }
                }
            };

        public void SubscribeConsumerEvents(IConsumerEvents<Ignore, byte[]> logConsumerEvents)
        {
            Consumer.SubscribeConsumerEvents(logConsumerEvents);
        }

        public async Task<CommittedOffsets> Commit()
        {
            return await Consumer.CommitAsync();
        }

        private Consumer<Ignore, byte[]> Consumer { get; set; }
        public ConsumerOptions ConsumerOptions { get; }

        public IDisposable Subscribe(IObserver<Message<Ignore, byte[]>> observer)
        {
            return observers.Add(observer);
        }

        private void OnNext(Message<Ignore, byte[]> message)
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
                if (Consumer.Consume(out Message<Ignore, byte[]> message, ConsumerOptions.PollInterval))
                {
                    OnNext(message);
                }
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

        private IEnumerable<IObserver<Message<Ignore, byte[]>>> SafeObservers()
        {
            return observers.ToEnumerable();
        }

        class ConsumerEvents : StubConsumerEvents<Ignore, byte[]>
        {
            private readonly ConsumerObservable consumerObservable;

            public ConsumerEvents(ConsumerObservable consumerObservable)
            {
                this.consumerObservable = consumerObservable ?? throw new ArgumentNullException(nameof(consumerObservable));
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
    }
}
