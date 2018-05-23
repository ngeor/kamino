using Confluent.Kafka;
using Microsoft.Extensions.Logging;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace BuzzStats.ListIngester
{
    public class MessagePublisher : IMessagePublisher
    {
        private readonly IRepository repository;
        private readonly ILogger logger;

        public MessagePublisher(IMessageConverter messageConverter, IRepository repository, ILogger logger)
        {
            MessageConverter = messageConverter;
            this.repository = repository;
            this.logger = logger;
        }

        private IMessageConverter MessageConverter { get; }

        public IEnumerable<string> HandleMessage(string inputMessage)
        {
            return Task.Run(async () =>
            {
                return await HandleMessageAsync(inputMessage);
            }).GetAwaiter().GetResult();
        }

        public async Task<IEnumerable<string>> HandleMessageAsync(string inputMessage)
        {
            List<string> result = new List<string>();
            logger.LogInformation("Fetching {0}", inputMessage);
            foreach (string outputMessage in await MessageConverter.ConvertAsync(inputMessage))
            {
                if (await repository.AddIfMissing(outputMessage))
                {
                    logger.LogInformation("Publishing story {0}", outputMessage);
                    result.Add(outputMessage);
                }
                else
                {
                    logger.LogInformation("Story {0} already exists", outputMessage);
                }
            }

            return result;
        }
    }
}
