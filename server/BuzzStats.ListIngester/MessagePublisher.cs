using Confluent.Kafka;
using log4net;
using System.Threading.Tasks;

namespace BuzzStats.ListIngester
{
    public class MessagePublisher : IMessagePublisher
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof(MessagePublisher));

        public MessagePublisher(IMessageConverter messageConverter, ISerializingProducer<Null, string> producer, string outputTopic)
        {
            MessageConverter = messageConverter;
            Producer = producer;
            OutputTopic = outputTopic;
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
                Log.InfoFormat("Publishing story {0} to topic {1}", outputMessage, OutputTopic);
                await Producer.ProduceAsync(OutputTopic, null, outputMessage);
            }
        }
    }
}
