using System.Threading.Tasks;

namespace BuzzStats.ListIngester
{
    public interface IMessagePublisher
    {
        void HandleMessage(string inputMessage);
        Task HandleMessageAsync(string inputMessage);
    }
}