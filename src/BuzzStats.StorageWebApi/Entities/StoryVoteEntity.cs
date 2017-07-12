using System;

namespace BuzzStats.StorageWebApi.Entities
{
    public class StoryVoteEntity
    {
        public virtual int Id { get; set; }

        public virtual StoryEntity Story { get; set; }

        public virtual string Username { get; set; }

        public virtual DateTime CreatedAt { get; set; }

        public override string ToString()
        {
            return string.Format(
                "{0} Id={1} Username={2} CreatedAt={3} Story=[{4}]",
                GetType().Name,
                Id,
                Username,
                CreatedAt,
                Story);
        }
    }
}