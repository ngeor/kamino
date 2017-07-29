using System.Threading.Tasks;
using BuzzStats.CrawlerService.DTOs;

namespace BuzzStats.CrawlerService
{
    public interface IStorageClient
    {
        Task Save(Story story);
    }
}