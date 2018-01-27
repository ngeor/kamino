using System.Collections.Generic;
using System.Threading.Tasks;
using BuzzStats.DTOs;

namespace BuzzStats.ChangeTracker
{
    public interface IEventProducer
    {
        Task<IEnumerable<StoryEvent>> CreateEventsAsync(Story parsedStory);
    }
}