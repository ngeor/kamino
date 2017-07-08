using System;

namespace BuzzStats.StorageWebApi.Entities
{
    public interface IEntity
    {
        int Id { get; }
        DateTime CreatedAt { get; }
    }
}