using System.Collections.Generic;
using System.Threading.Tasks;

namespace BuzzStats.ListIngester
{
    public interface IMessagePublisher
    {
        IEnumerable<string> HandleMessage(string inputMessage);
        Task<IEnumerable<string>> HandleMessageAsync(string inputMessage);
    }
}