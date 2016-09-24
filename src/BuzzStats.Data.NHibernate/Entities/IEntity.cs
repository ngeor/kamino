using System;

namespace BuzzStats.Data.NHibernate.Entities
{
    public interface IEntity
    {
        int Id { get; }
        DateTime CreatedAt { get; }
    }
}
