using MongoDB.Bson;
using System;
using System.Linq;

namespace BuzzStats.WebApi.DTOs
{
    public class StoryWithRecentComments
    {
        public ObjectId Id { get; set; }
        public int StoryId { get; set; }
        public string Title { get; set; }
        public RecentComment[] Comments { get; set; }

        public override string ToString()
        {
            return $"StoryId: {StoryId}, Title: {Title}, Comments: {Comments}";
        }

        public override bool Equals(object obj)
        {
            StoryWithRecentComments that = obj as StoryWithRecentComments;
            if (that == null)
            {
                return false;
            }

            return StoryId == that.StoryId && string.Equals(Title, that.Title)
                   && ((Comments == null && that.Comments == null) ||
                       (Comments != null && that.Comments != null && Comments.SequenceEqual(that.Comments)));
        }

        public override int GetHashCode()
        {
            unchecked
            {
                int result = StoryId;
                result = result * 7 + (Title ?? "").GetHashCode();
                if (Comments != null)
                {
                    foreach (var c in Comments)
                    {
                        result = result * 11 + c.GetHashCode();
                    }
                }

                return result;
            }
        }
    }
}