//
//  IStoryQueryDateFilter.cs
//
//  Author:
//       ngeor
//
//  Copyright (c) 2014 ngeor

using NodaTime;

namespace BuzzStats.Data
{
    public interface IStoryQueryDateFilter
    {
        IStoryQuery StoryQuery { get; }
        IStoryQuery InRange(DateInterval dateInterval);
    }
}