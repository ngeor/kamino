using Autofac;
using BuzzStats.Kafka;
using BuzzStats.ListIngester.Mongo;
using BuzzStats.Logging;
using BuzzStats.Parsing;
using Confluent.Kafka;
using Confluent.Kafka.Serialization;
using log4net;
using NodaTime;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Reflection;
using System.Text;
using System.Threading.Tasks;
using Yak.Configuration;
using Yak.Configuration.Autofac;
using Yak.Kafka;

namespace BuzzStats.ListIngester
{
    //class BaseObservable<T> : IObservable<T>
    //{
    //    private List<IObserver<T>> observers = new List<IObserver<T>>();

    //    public IDisposable Subscribe(IObserver<T> observer)
    //    {
    //        observers.Add(observer);
    //        return new Unsubscribe(this, observer);
    //    }

    //    protected void SendNext(T message)
    //    {
    //        foreach(var observer in observers)
    //        {
    //            observer.OnNext(message);
    //        }
    //    }

    //    protected void SendError(Exception error)
    //    {
    //        foreach (var observer in observers)
    //        {
    //            observer.OnError(error);
    //        }
    //    }

    //    protected void SendCompleted()
    //    {
    //        foreach (var observer in observers)
    //        {
    //            observer.OnCompleted();
    //        }
    //    }

    //    private void DoUnsubscribe(IObserver<T> observer)
    //    {
    //        observers.Remove(observer);
    //    }

    //    class Unsubscribe : IDisposable
    //    {
    //        public Unsubscribe(BaseObservable<T> observable, IObserver<T> observer)
    //        {
    //            Observable = observable;
    //            Observer = observer ?? throw new ArgumentNullException(nameof(observer));
    //        }

    //        public BaseObservable<T> Observable { get; }
    //        public IObserver<T> Observer { get; }

    //        public void Dispose()
    //        {
    //            Observable.DoUnsubscribe(Observer);
    //        }
    //    }
    //}

    //class PipeObserver<TIn, TOut> : BaseObservable<TOut>, IObserver<TIn>
    //{
    //    public PipeObserver(Func<TIn, TOut> converter)
    //    {
    //        Converter = converter;
    //    }

    //    public Func<TIn, TOut> Converter { get; }

    //    public void OnCompleted()
    //    {
    //        SendCompleted();
    //    }

    //    public void OnError(Exception error)
    //    {
    //        SendError(error);
    //    }

    //    public void OnNext(TIn value)
    //    {
    //        SendNext(Converter(value));
    //    }
    //}

    //class TapObserver<T> : PipeObserver<T, T>
    //{
    //    public TapObserver(Action<T> tapAction)
    //        : base(x =>
    //        {
    //            tapAction(x);
    //            return x;
    //        })
    //    {
    //    }
    //}

    //class ConsumerObservable : BaseObservable<byte[]>
    //{
    //    protected static readonly ILog Log = LogManager.GetLogger(
    //        Assembly.GetEntryAssembly(), typeof(ConsumerObservable));

    //    public void Poll()
    //    {
    //        using (var consumer = new Consumer<Null, byte[]>(
    //            ConstructConfig(true),
    //            new NullDeserializer(),
    //            new ByteArrayDeserializer()))
    //        {
    //            // Note: All event handlers are called on the main thread.

    //            consumer.OnMessage += (_, msg)
    //                =>
    //            {
    //                SendNext(msg.Value);
    //            };

    //            consumer.OnPartitionEOF += (_, end)
    //                => Log.Debug($"Reached end of topic {end.Topic} partition {end.Partition}, next message will be at offset {end.Offset}");

    //            // Raised on critical errors, e.g. connection failures or all brokers down.
    //            consumer.OnError += (_, error)
    //                => Log.Error($"Error: {error}");

    //            // Raised on deserialization errors or when a consumed message has an error != NoError.
    //            consumer.OnConsumeError += (_, msg)
    //                => Log.Error($"Error consuming from topic/partition/offset {msg.Topic}/{msg.Partition}/{msg.Offset}: {msg.Error}");

    //            consumer.OnOffsetsCommitted += (_, commit) =>
    //            {
    //                Log.Debug($"[{string.Join(", ", commit.Offsets)}]");

    //                if (commit.Error)
    //                {
    //                    Log.Error($"Failed to commit offsets: {commit.Error}");
    //                }

    //                Log.Debug($"Successfully committed offsets: [{string.Join(", ", commit.Offsets)}]");
    //            };

    //            consumer.OnPartitionsAssigned += (_, partitions) =>
    //            {
    //                Log.Info($"Assigned partitions: [{string.Join(", ", partitions)}], member id: {consumer.MemberId}");
    //                consumer.Assign(partitions);
    //            };

    //            consumer.OnPartitionsRevoked += (_, partitions) =>
    //            {
    //                Log.Info($"Revoked partitions: [{string.Join(", ", partitions)}]");
    //                consumer.Unassign();
    //            };

    //            consumer.OnStatistics += (_, json)
    //                => Log.Debug($"Statistics: {json}");

    //            consumer.Subscribe(topic);

    //            Log.Info($"Subscribed to: [{string.Join(", ", consumer.Subscription)}]");

    //            if (HandleCancelKeyPress)
    //            {
    //                Console.CancelKeyPress += (_, e) =>
    //                {
    //                    e.Cancel = true; // prevent the process from terminating.
    //                    IsCancelled = true;
    //                };

    //                Console.WriteLine("Ctrl-C to exit.");
    //            }

    //            Log.Info("Polling started");

    //            while (!IsCancelled)
    //            {
    //                consumer.Poll(TimeSpan.FromMilliseconds(100));
    //            }

    //            Log.Info("Polling stopped");
    //        }
    //    }

    //    private Dictionary<string, object> ConstructConfig(bool enableAutoCommit) =>
    //        new Dictionary<string, object>
    //        {
    //            { "group.id", ConsumerId },
    //            { "enable.auto.commit", enableAutoCommit },
    //            { "auto.commit.interval.ms", 5000 },
    //            { "statistics.interval.ms", 60000 },
    //            { "bootstrap.servers", BrokerList },
    //            { "default.topic.config", new Dictionary<string, object>()
    //                {
    //                    { "auto.offset.reset", "smallest" }
    //                }
    //            }
    //        };
    //}

    //public static class ObservableExtensions
    //{
    //    public static IObservable<TOut> Map<TIn, TOut>(this IObservable<TIn> observable, Func<TIn, TOut> converter)
    //    {
    //        var result = new PipeObserver<TIn, TOut>(converter);
    //        observable.Subscribe(result);
    //        return result;
    //    }

    //    public static IObservable<T> Tap<T>(this IObservable<T> observable, Action<T> tapAction)
    //    {
    //        var result = new TapObserver<T>(tapAction);
    //        observable.Subscribe(result);
    //        return result;
    //    }
    //}

    //class Test
    //{
    //    void Test1()
    //    {
    //        new ConsumerObservable()
    //            .Map(Encoding.UTF8.GetString)
    //            .Tap(Console.WriteLine)
    //            .Subscribe(Producer);
    //    }
    //}


    public class Program
    {
        const string InputTopic = "ListExpired";
        const string OutputTopic = "StoryExpired";

        [ConfigurationValue]
        private string brokerList = "127.0.0.1";

        public Program(IMessageConverter messageConverter, IRepository repository)
        {
            MessageConverter = messageConverter ?? throw new ArgumentNullException(nameof(messageConverter));
            Repository = repository ?? throw new ArgumentNullException(nameof(repository));
            LogSetup.Setup();
        }

        private IMessageConverter MessageConverter { get; }
        private IRepository Repository { get; }

        public void Start()
        {
            Console.WriteLine("Starting List Ingester");

            var consumer = new ConsumerApp<Null, string>(brokerList, "BuzzStats.ListIngester", new NullDeserializer(), Deserializers.String());

            using (var producer = new ProducerBuilder<Null, string>(brokerList, null, Serializers.String()).Build())
            {
                var messagePublisher = new MessagePublisher(
                    MessageConverter,
                    producer,
                    OutputTopic,
                    Repository);

                consumer.MessageReceived += (_, msg) =>
                {
                    messagePublisher.HandleMessage(msg.Value);
                };

                // TODO command line argument and/or environment variable for number of pages to go through
                const int pageNumber = 4;
                using (Cron cron = new Cron(
                    messagePublisher,
                    TimeSpan.FromSeconds(5),
                    TimeSpan.FromMinutes(1),
                    PageBuilder.Build(pageNumber).ToArray()))
                {
                    consumer.Poll(InputTopic);
                }
            }
        }

        static void Main(string[] args)
        {
            var builder = new ContainerBuilder();
            builder.RegisterType<Program>()
                .InjectConfiguration();

            builder.RegisterType<ParserClient>().As<IParserClient>();
            builder.RegisterType<MessageConverter>().As<IMessageConverter>();
            builder.RegisterType<Parser>().As<IParser>();
            builder.Register(c => new UrlProvider("http://buzz.reality-tape.com/")).As<IUrlProvider>();
            builder.Register(c => SystemClock.Instance).As<IClock>();
            builder.RegisterType<Repository>()
                .As<IRepository>()
                .InjectConfiguration();

            var container = builder.Build();
            using (var scope = container.BeginLifetimeScope())
            {
                var program = scope.Resolve<Program>();
                program.Start();
            }
        }
    }

    //class Processor
    //{
    //    // TODO: should be as simple as this method
    //    public async Task<IEnumerable<int>> ListExpired(Tuple<StoryListing, int> storyListingPage)
    //    {
    //        return null;
    //    }
    //}
}
