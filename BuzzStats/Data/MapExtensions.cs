using NGSoftware.Common;

namespace BuzzStats.Data
{
    public static class MapExtensions
    {
        public static RecentlyCommentedStory ToRecentlyCommentedStory(this StoryData story)
        {
            return story == null
                ? null
                : new RecentlyCommentedStory
                {
                    StoryId = story.StoryId,
                    Title = story.Title
                };
        }

        public static RecentlyCommentedStory.Comment ToRecentlyCommentedStoryComment(this CommentData comment)
        {
            return comment == null
                ? null
                : new RecentlyCommentedStory.Comment
                {
                    Age = comment.CreatedAt.Age(),
                    CommentId = comment.CommentId,
                    StoryId = comment.StoryId,
                    Username = comment.Username,
                    VotesUp = comment.VotesUp
                };
        }

        public static CommentSummary ToCommentSummary(this CommentData comment)
        {
            return comment == null
                ? null
                : new CommentSummary
                {
                    Age = comment.CreatedAt.Age(),
                    CommentId = comment.CommentId,
                    Story = comment.Story.ToCommentSummaryParentStory(),
                    Username = comment.Username,
                    VotesUp = comment.VotesUp
                };
        }

        public static CommentSummary.ParentStory ToCommentSummaryParentStory(this StoryData story)
        {
            return story == null
                ? null
                : new CommentSummary.ParentStory
                {
                    StoryId = story.StoryId,
                    Title = story.Title
                };
        }

        public static StorySummary ToStorySummary(this StoryData story)
        {
            return story == null
                ? null
                : new StorySummary
                {
                    CreatedAt = story.CreatedAt,
                    LastCheckedAt = story.LastCheckedAt,
                    StoryId = story.StoryId,
                    Title = story.Title,
                    Username = story.Username,
                    VoteCount = story.VoteCount
                };
        }
    }
}