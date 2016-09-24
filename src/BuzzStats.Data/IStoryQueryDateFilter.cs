//
//  IStoryQueryDateFilter.cs
//
//  Author:
//       ngeor
//
//  Copyright (c) 2014 ngeor

using NGSoftware.Common;

namespace BuzzStats.Data
{
    public interface IStoryQueryDateFilter
    {
        IStoryQuery StoryQuery { get; }
        IStoryQuery InRange(DateRange dateRange);
    }
}
