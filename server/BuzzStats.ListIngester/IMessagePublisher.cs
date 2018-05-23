using BuzzStats.Parsing.DTOs;
using System.Threading.Tasks;

namespace BuzzStats.ListIngester
{
    public interface IMessagePublisher
    {
        StoryListingSummary HandleMessage(StoryListingSummary inputMessage);
        Task<StoryListingSummary> HandleMessageAsync(StoryListingSummary inputMessage);
    }
}