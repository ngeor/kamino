using System;
using NGSoftware.Common;

namespace BuzzStats.Data
{
    public static class StoryQueryDateFilterExtensions
    {
        public static IStoryQuery Before(this IStoryQueryDateFilter filter, DateTime stop)
        {
            return filter.InRange(DateRange.Before(stop));
        }

        public static IStoryQuery InRange(this IStoryQueryDateFilter filter, TimeSpanRange timeSpanRange)
        {
            if (timeSpanRange.IsEmpty)
            {
                return filter.StoryQuery;
            }

            return filter.InRange(timeSpanRange.ToAgeDateRange(TestableDateTime.UtcNow));
        }

        public static IStoryQuery InRange(this IStoryQueryDateFilter filter, TimeSpanRange? timeSpanRange)
        {
            if (!timeSpanRange.HasValue)
            {
                return filter.StoryQuery;
            }

            return filter.InRange(timeSpanRange.Value);
        }
    }
}