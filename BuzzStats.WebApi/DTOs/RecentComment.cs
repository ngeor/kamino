namespace BuzzStats.WebApi.DTOs
{
    public class RecentComment
    {
        public int CommentId { get; set; }
        public string Username { get; set; }
        public int VotesUp { get; set; }

        public override string ToString()
        {
            return $"CommentId: {CommentId}, Username: {Username}, VotesUp: {VotesUp}";
        }

        public override bool Equals(object obj)
        {
            RecentComment that = obj as RecentComment;
            if (that == null)
            {
                return false;
            }
            
            return CommentId == that.CommentId && VotesUp == that.VotesUp
                   && string.Equals(Username, that.Username);
        }

        public override int GetHashCode()
        {
            unchecked
            {
                int result = CommentId;
                result = result * 7 + VotesUp;
                result = result * 13 + (Username != null ? Username.GetHashCode() : 0);
                return result;
            }
        }
    }
}