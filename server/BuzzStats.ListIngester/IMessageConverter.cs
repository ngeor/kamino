using System.Collections.Generic;
using System.Threading.Tasks;

namespace BuzzStats.ListIngester
{
    public interface IMessageConverter
    {
        Task<IEnumerable<string>> ConvertAsync(string msg);
    }
}