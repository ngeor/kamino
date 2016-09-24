// --------------------------------------------------------------------------------
// <copyright file="StoryQueryDSL.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/10/17
// * Time: 10:43:55
// --------------------------------------------------------------------------------

using System.Collections.Generic;
using Moq;
using NGSoftware.Common;
using BuzzStats.Data;

namespace BuzzStats.Tests.DSL
{
    public static class StoryQueryDSL
    {
        public static IStoryQuery SetupExcludeIds(this IStoryQuery storyQuery, IEnumerable<int> ids)
        {
            Mock.Get<IStoryQuery>(storyQuery).Setup(p => p.ExcludeIds(ids)).Returns(storyQuery);
            return storyQuery;
        }

        public static IStoryQuery SetupTake(this IStoryQuery storyQuery, int take)
        {
            Mock.Get<IStoryQuery>(storyQuery).Setup(p => p.Take(take)).Returns(storyQuery);
            return storyQuery;
        }

        public static IStoryQuery SetupOrderBy(this IStoryQuery storyQuery, EnumSortExpression<StorySortField> sort)
        {
            Mock.Get<IStoryQuery>(storyQuery).Setup(p => p.OrderBy(sort)).Returns(storyQuery);
            return storyQuery;
        }

        public static IStoryQuery ReturnsEnumerableOfIds(this IStoryQuery storyQuery, IEnumerable<int> ids)
        {
            Mock.Get<IStoryQuery>(storyQuery).Setup(p => p.AsEnumerableOfIds()).Returns(ids);
            return storyQuery;
        }

        public static IStoryDataLayer BindStoryDataLayer(this IStoryQuery storyQuery)
        {
            return Mock.Of<IStoryDataLayer>(s => s.Query() == storyQuery);
        }

        public static IDbSession BindDbSession(this IStoryDataLayer storyDataLayer)
        {
            return Mock.Of<IDbSession>(s => s.Stories == storyDataLayer);
        }

        public static IDbContext BindDbContext(this IDbSession dbSession)
        {
            return Mock.Of<IDbContext>(s => s.OpenSession() == dbSession);
        }

        public static IDbContext BindDbContext(this IStoryQuery storyQuery)
        {
            return storyQuery.BindStoryDataLayer().BindDbSession().BindDbContext();
        }

        public static IDbContext BindDbContext(this IStoryDataLayer storyDataLayer)
        {
            return storyDataLayer.BindDbSession().BindDbContext();
        }

        public static IDbSession BindDbSession(this IStoryPollHistoryDataLayer storyPollHistoryDataLayer)
        {
            return Mock.Of<IDbSession>(s => s.StoryPollHistories == storyPollHistoryDataLayer);
        }

        public static IDbContext BindDbContext(this IStoryPollHistoryDataLayer storyPollHistoryDataLayer)
        {
            return storyPollHistoryDataLayer.BindDbSession().BindDbContext();
        }
    }
}
