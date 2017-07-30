//
//  StoryQueryDateFilter.cs
//
//  Author:
//       ngeor
//
//  Copyright (c) 2014 ngeor

using NGSoftware.Common;
using NodaTime;

namespace BuzzStats.Data.NHibernate
{
    internal class StoryQueryDateFilter : IStoryQueryDateFilter
    {
        private readonly StoryQuery _storyQuery;

        public StoryQueryDateFilter(StoryQuery storyQuery)
        {
            _storyQuery = storyQuery;
        }

        public IStoryQuery StoryQuery
        {
            get { return _storyQuery; }
        }

        internal DateInterval DateInterval { get; private set; }

        public IStoryQuery InRange(DateInterval dateInterval)
        {
            DateInterval = dateInterval;
            return _storyQuery;
        }
    }
}