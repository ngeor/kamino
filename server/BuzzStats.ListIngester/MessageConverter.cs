using System;
using BuzzStats.Parsing;

namespace BuzzStats.ListIngester
{
    public class MessageConverter : IMessageConverter
    {
        public Tuple<StoryListing, int> Parse(string msg)
        {
            string[] parts = msg.Split(' ');
            if (parts.Length <= 0 || !Enum.TryParse(parts[0], out StoryListing storyListing))
            {
                storyListing = StoryListing.Home;
            }

            if (parts.Length <= 1 || !int.TryParse(parts[1], out int page))
            {
                page = 0;
            }

            return Tuple.Create(storyListing, page);
        }
    }
}
