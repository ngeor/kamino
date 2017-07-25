using BuzzStats.StorageWebApi.Entities;

namespace BuzzStats.StorageWebApi.DTOs
{
    public class CommentWithStory
    {
        public CommentWithStory()
        {
            
        }

        public CommentWithStory(CommentEntity comment)
        {
            CommentId = comment.CommentId;
            StoryId = comment.Story.StoryId;
        }

        public int CommentId { get; set; }
        public int StoryId { get; set; }
    }
}