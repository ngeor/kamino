using System;
using NodaTime;
using NodaTime.Extensions;

namespace BuzzStats.Data
{
    public static class StoryQueryDateFilterExtensions
    {
        public static IStoryQuery Before(this IStoryQueryDateFilter filter, DateTime stop)
        {
            return filter.InRange(new DateInterval(LocalDate.MinIsoValue, stop.ToLocalDateTime().Date));
        }
    }
}