using System.Threading.Tasks;

namespace BuzzStats.ListIngester
{
    public interface IRepository
    {
        Task<bool> AddIfMissing(string storyId);
    }
}