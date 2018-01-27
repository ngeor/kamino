using System.Threading.Tasks;

namespace BuzzStats.ListIngester
{
    public interface IMongoRepository
    {
        Task<bool> AddIfMissing(string storyId);
    }
}