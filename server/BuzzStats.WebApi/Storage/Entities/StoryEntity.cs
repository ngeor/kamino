using System;

namespace BuzzStats.WebApi.Storage.Entities
{
    public class StoryEntity
    {
        public virtual int Id { get; set; }

        public virtual int StoryId { get; set; }

        public virtual string Title { get; set; }

        public virtual string Url { get; set; }

        public virtual string Username { get; set; }

        public virtual DateTime CreatedAt { get; set; }

        public virtual int Category { get; set; }
    }
}