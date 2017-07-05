using System;

namespace BuzzStats.Data.NHibernate.Entities
{
    public class StoryPollHistoryEntity
    {
        public virtual StoryEntity Story { get; set; }

        public virtual string SourceId { get; set; }

        public virtual DateTime CheckedAt { get; set; }

        public virtual int HadChanges { get; set; }
    }
}