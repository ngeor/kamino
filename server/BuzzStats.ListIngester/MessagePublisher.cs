using BuzzStats.Parsing.DTOs;
using Microsoft.Extensions.Logging;
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

        public StoryListingSummary HandleMessage(StoryListingSummary inputMessage)
        {
            return Task.Run(async () =>
            {
                return await HandleMessageAsync(inputMessage);
            }).GetAwaiter().GetResult();
        }

        public async Task<StoryListingSummary> HandleMessageAsync(StoryListingSummary inputMessage)
        {
            if (await repository.AddIfMissing(inputMessage.StoryId))
            {
                logger.LogInformation("Story {0} is new", inputMessage.StoryId);
                return inputMessage;
            }
            else
            {
                logger.LogInformation("Story {0} already exists", inputMessage.StoryId);
                return null;
            }
        }
    }
}
