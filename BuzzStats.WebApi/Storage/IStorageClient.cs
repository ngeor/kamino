using BuzzStats.WebApi.DTOs;

namespace BuzzStats.WebApi.Storage
{
    public interface IStorageClient
    {
        void Save(Story story);
    }
}