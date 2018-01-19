using BuzzStats.WebApi.DTOs;
using Confluent.Kafka;
using Confluent.Kafka.Serialization;
using System.Collections.Generic;
using System.Text;

namespace BuzzStats.WebApi.Crawl
{
    public class StoryProcessTopic : IStoryProcessTopic
    {
        private readonly IAsyncQueue<StoryListingSummary> _queue;

        public StoryProcessTopic(IAsyncQueue<StoryListingSummary> queue)
        {
            _queue = queue;
        }

        public void Post(StoryListingSummary storyListingSummary)
        {
            _queue.Push(storyListingSummary);

            var config = new Dictionary<string, object>
            {
                { "bootstrap.servers", "192.168.99.100" }
            };

            using (var producer = new Producer<Null, string>(
                config,
                null,
                new StringSerializer(Encoding.UTF8)
                ))
            {
                // Blocking call!
                var result = producer.ProduceAsync("StoryFound", null, "Story " + storyListingSummary.StoryId + " found").Result;

            }
        }
    }
}