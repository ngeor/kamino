// --------------------------------------------------------------------------------
// <copyright file="IStoriesPageView.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2014
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2014/08/09
// * Time: 19:15:02
// --------------------------------------------------------------------------------

using System.Collections.Generic;
using BuzzStats.Data;

namespace BuzzStats.Web.Mvp
{
    public interface IStoriesPageView : IView
    {
        void SetCommentCount(int commentCount);
        void SetStories(StorySortField sortField, ICollection<StorySummary> stories);
        void SetStoryCount(int nonRemovedStoryCount, int removedStoryCount);
    }
}
