using System.Threading.Tasks;
using BuzzStats.WebApi.DTOs;

namespace BuzzStats.WebApi
{
    public interface IStorageClient
    {
        Task Save(Story story);
    }
}