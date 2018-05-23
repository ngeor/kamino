using Confluent.Kafka;
using Microsoft.Extensions.Logging;
using System;
using System.Threading.Tasks;

namespace BuzzStats.StoryUpdater
{
    public class OldestStoryUpdater
    {
        private readonly IRepository repository;
        private readonly ISerializingProducer<Null, string> producer;
        private readonly string outputTopic;
        private readonly ILogger logger;

        public OldestStoryUpdater(
            IRepository repository,
            ISerializingProducer<Null, string> producer,
            string outputTopic,
            ILogger logger)
        {
            this.repository = repository ?? throw new ArgumentNullException(nameof(repository));
            this.producer = producer ?? throw new ArgumentNullException(nameof(producer));
            this.outputTopic = outputTopic ?? throw new ArgumentNullException(nameof(outputTopic));
            this.logger = logger;
        }

        public async Task UpdateAsync()
        {
            var storyId = await repository.OldestCheckedStory();
            if (!storyId.HasValue)
            {
                logger.LogInformation("No stories are known to story updater!");
                return;
            }

            logger.LogInformation("Oldest checked story is {0}", storyId);
            await producer.ProduceAsync(outputTopic, null, storyId.ToString());
            await repository.UpdateLastCheckedDate(storyId.Value);
        }

        public void Update()
        {
            Task.Run(async () => await UpdateAsync())
                .GetAwaiter()
                .GetResult();
        }
    }
}
