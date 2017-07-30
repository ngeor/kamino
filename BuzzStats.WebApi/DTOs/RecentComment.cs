using System;
using NGSoftware.Common;

namespace BuzzStats.WebApi.DTOs
{
    public class RecentComment
    {
        public int CommentId { get; set; }
        public string Username { get; set; }
        public int VotesUp { get; set; }
        public DateTime CreatedAt { get; set; }

        public override string ToString()
        {
            return $"CommentId: {CommentId}, Username: {Username}, VotesUp: {VotesUp}, CreatedAt: {CreatedAt}";
        }

        public override bool Equals(object obj)
        {
            RecentComment that = obj as RecentComment;
            if (that == null)
            {
                return false;
            }

            return CommentId == that.CommentId && VotesUp == that.VotesUp
                   && string.Equals(Username, that.Username)
                   && CreatedAt == that.CreatedAt;
        }

        public override int GetHashCode()
        {
            unchecked
            {
                int result = CommentId;
                result = result * 7 + VotesUp;
                result = result * 11 + Username.SafeHashCode();
                result = result * 13 + CreatedAt.GetHashCode();
                return result;
            }
        }
    }
}