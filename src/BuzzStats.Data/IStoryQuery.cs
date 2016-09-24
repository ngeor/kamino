// --------------------------------------------------------------------------------
// <copyright file="IStoryQuery.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/10/04
// * Time: 10:03 μμ
// --------------------------------------------------------------------------------

using System.Collections.Generic;
using NGSoftware.Common;

namespace BuzzStats.Data
{
    public interface IStoryQuery : IEntityQuery<StoryData>
    {
        IStoryQueryDateFilter CreatedAt { get; }
        IStoryQueryDateFilter LastCheckedAt { get; }
        IStoryQueryDateFilter LastModifiedAt { get; }

        IStoryQuery Username(string username);

        IStoryQuery ExcludeIds(IEnumerable<int> storyIds);

        IStoryQuery OrderBy(EnumSortExpression<StorySortField> sortExpression);
        IStoryQuery ThenBy(EnumSortExpression<StorySortField> sortExpression);

        IStoryQuery Skip(int skip);
        IStoryQuery Take(int count);
    }
}
