namespace BuzzStats.ParserWebApi.DTOs
{
    public sealed class StoryListingSummary
    {
        public StoryListingSummary()
        {
        }

        public StoryListingSummary(int storyId)
        {
            StoryId = storyId;
        }

        public StoryListingSummary(int storyId, int? voteCount)
        {
            StoryId = storyId;
            VoteCount = voteCount;
        }

        public int StoryId { get; set; }

        public int? VoteCount { get; set; }

        public override string ToString()
        {
            return string.Format("[StorySummary: StoryId={0}, VoteCount={1}]", StoryId, VoteCount);
        }
    }
}