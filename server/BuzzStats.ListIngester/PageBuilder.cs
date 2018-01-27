using BuzzStats.Parsing;
using System;
using System.Collections.Generic;

namespace BuzzStats.ListIngester
{
    public static class PageBuilder
    {
        public static IEnumerable<string> Build(int pageCount)
        {
            var values = Enum.GetValues(typeof(StoryListing));
            for (var i = 1; i <= pageCount; i++)
            {
                foreach (var v in values)
                {
                    yield return i == 1 ? v.ToString() : $"{v} {i}";
                }
            }
        }
    }
}
