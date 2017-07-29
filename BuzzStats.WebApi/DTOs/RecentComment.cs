namespace BuzzStats.WebApi.DTOs
{
    public class RecentComment
    {
        public int CommentId { get; set; }
        public string User { get; set; }
        public int VotesUp { get; set; }
    }
}