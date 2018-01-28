using Confluent.Kafka;
using log4net;
using System.Threading.Tasks;

namespace BuzzStats.ListIngester
{
    public class MessagePublisher : IMessagePublisher
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof(MessagePublisher));
        private readonly IRepository repository;

        public MessagePublisher(IMessageConverter messageConverter, ISerializingProducer<Null, string> producer, string outputTopic, IRepository repository)
        {
            MessageConverter = messageConverter;
            Producer = producer;
            OutputTopic = outputTopic;
            this.repository = repository;
        }

        private IMessageConverter MessageConverter { get; }
        private ISerializingProducer<Null, string> Producer { get; }
        private string OutputTopic { get; }

        public void HandleMessage(string inputMessage)
        {
            Task.Run(async () =>
            {
                await HandleMessageAsync(inputMessage);
            }).GetAwaiter().GetResult();
        }

        public async Task HandleMessageAsync(string inputMessage)
        {
            Log.InfoFormat("Fetching {0}", inputMessage);
            foreach (string outputMessage in await MessageConverter.ConvertAsync(inputMessage))
            {
                if (await repository.AddIfMissing(outputMessage))
                {
                    Log.InfoFormat("Publishing story {0} to topic {1}", outputMessage, OutputTopic);
                    await Producer.ProduceAsync(OutputTopic, null, outputMessage);
                }
                else
                {
                    Log.InfoFormat("Story {0} already exists", outputMessage);
                }
            }
        }
    }
}
