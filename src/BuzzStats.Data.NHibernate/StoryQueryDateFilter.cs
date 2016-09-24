//
//  StoryQueryDateFilter.cs
//
//  Author:
//       ngeor
//
//  Copyright (c) 2014 ngeor

using NGSoftware.Common;

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

        internal DateRange DateRange { get; private set; }

        public IStoryQuery InRange(DateRange dateRange)
        {
            DateRange = dateRange;
            return _storyQuery;
        }
    }
}
