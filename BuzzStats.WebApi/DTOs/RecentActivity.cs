using System;

namespace BuzzStats.WebApi.DTOs
{
    /// <summary>
    /// Recent activity data object.
    /// </summary>
    public class RecentActivity
    {
        public int StoryId { get; set; }
        public int CommentId { get; set; }
        public string Title { get; set; }
        public DateTime CreatedAt { get; set; }

        public string StoryUsername { get; set; }
        public DateTime StoryCreatedAt { get; set; }

        public string StoryVoteUsername { get; set; }

        public string CommentUsername { get; set; }
        public DateTime CommentCreatedAt { get; set; }
    }
}
