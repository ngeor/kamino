using System;

namespace BuzzStats.StorageWebApi.Entities
{
    public class StoryEntity : IEntity
    {
        public virtual int Id { get; set; }

        public virtual int StoryId { get; set; }

        public virtual string Title { get; set; }

        public virtual string Url { get; set; }

        public virtual string Username { get; set; }

        public virtual DateTime CreatedAt { get; set; }

        public virtual DateTime LastCheckedAt { get; set; }

        public virtual int Category { get; set; }

        /// <summary>
        /// Gets or sets the date when the story was detected from the system.
        /// </summary>
        /// <remarks>
        /// The <see cref="CreatedAt"/> date time is the date when Buzz reports that the story was created.
        /// The <see cref="DetectedAt"/> date time is the date when our system detected the story.
        /// The difference between these two dates should be low and is a measurement of system's accuracy.
        /// </remarks>
        public virtual DateTime DetectedAt { get; set; }

        public virtual DateTime LastModifiedAt { get; set; }

        public virtual int TotalUpdates { get; set; }

        public virtual int TotalChecks { get; set; }

        public virtual DateTime? LastCommentedAt { get; set; }

        public virtual string Host { get; set; }

        public virtual int VoteCount { get; set; }

        public virtual DateTime? RemovedAt { get; set; }
    }
}