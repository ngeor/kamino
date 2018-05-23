using System.Collections.Generic;

namespace BuzzStats.Parsing.DTOs
{
    public class StoryListingSummaries : List<StoryListingSummary>
    {
        public StoryListingSummaries()
        { }

        public StoryListingSummaries(IEnumerable<StoryListingSummary> elements)
            : base(elements)
        {
        }
    }
}