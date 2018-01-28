using System;

namespace BuzzStats.Web.Mongo
{
    public class RecentComment
    {
        public int CommentId { get; set; }
        public string Username { get; set; }
        public int VotesUp { get; set; }
        public DateTime CreatedAt { get; set; }
    }
}