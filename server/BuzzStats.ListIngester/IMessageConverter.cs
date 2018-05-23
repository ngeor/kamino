using System;
using BuzzStats.Parsing;

namespace BuzzStats.ListIngester
{
    public interface IMessageConverter
    {
        Tuple<StoryListing, int> Parse(string msg);
    }
}
