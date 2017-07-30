namespace BuzzStats.WebApi.DTOs
{
    public class CommentWithStory
    {
        public int CommentId { get; set; }
        public int StoryId { get; set; }
        public string Title { get; set; }
        public string Username { get; set; }
        public int VotesUp { get; set; }
    }
}