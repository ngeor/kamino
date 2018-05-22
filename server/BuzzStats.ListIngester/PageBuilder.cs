using BuzzStats.Parsing;
using System;
using System.Collections.Generic;

namespace BuzzStats.ListIngester
{
    public static class PageBuilder
    {
        public static IEnumerable<string> Build(int pageCount)
        {
            var storyListingValues = Enum.GetValues(typeof(StoryListing));
            for (var pageNumber = 1; pageNumber <= pageCount; pageNumber++)
            {
                foreach (var storyListing in storyListingValues)
                {
                    yield return pageNumber == 1 ? storyListing.ToString() : $"{storyListing} {pageNumber}";
                }
            }
        }
    }
}
