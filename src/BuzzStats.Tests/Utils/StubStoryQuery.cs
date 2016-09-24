using System;
using System.Collections.Generic;
using System.Linq;
using Moq;
using NGSoftware.Common;
using BuzzStats.Data;

namespace BuzzStats.Tests.Utils
{
    public class StubStoryQuery : IStoryQuery
    {
        public static IStoryQuery Mock()
        {
            var mock = new Mock<StubStoryQuery>();
            mock.CallBase = true;
            return mock.Object;
        }

        public IStoryQueryDateFilter CreatedAt
        {
            get { throw new NotImplementedException(); }
        }

        public IStoryQueryDateFilter LastCheckedAt
        {
            get { throw new NotImplementedException(); }
        }

        public IStoryQueryDateFilter LastModifiedAt
        {
            get { throw new NotImplementedException(); }
        }

        public IEnumerable<StoryData> AsEnumerable()
        {
            return Enumerable.Empty<StoryData>();
        }

        public IEnumerable<int> AsEnumerableOfIds()
        {
            return Enumerable.Empty<int>();
        }

        public int Count()
        {
            return 0;
        }

        public IStoryQuery ExcludeIds(IEnumerable<int> storyIds)
        {
            return this;
        }

        public IStoryQuery OrderBy(EnumSortExpression<StorySortField> sortExpression)
        {
            return this;
        }

        public IStoryQuery Skip(int skip)
        {
            return this;
        }

        public IStoryQuery Take(int count)
        {
            return this;
        }

        public IStoryQuery ThenBy(EnumSortExpression<StorySortField> sortExpression)
        {
            return this;
        }

        public IStoryQuery Username(string username)
        {
            return this;
        }
    }
}
