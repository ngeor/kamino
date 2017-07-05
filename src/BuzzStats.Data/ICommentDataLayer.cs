// --------------------------------------------------------------------------------
// <copyright file="ICommentDataLayer.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/03/28
// * Time: 12:54:11
// --------------------------------------------------------------------------------

using System.Collections.Generic;
using NGSoftware.Common;

namespace BuzzStats.Data
{
    public interface ICommentDataLayer
    {
        #region CRUD

        CommentData Create(CommentData newComment);
        CommentData Read(int commentBusinessId);
        void Update(CommentData existingComment);

        #endregion

        #region Querying

        CommentData[] Query(StoryData story, CommentData parentComment);
        CommentData[] Query(CommentDataQueryParameters queryParameters);

        #endregion

        #region Stats

        int Count(DateRange dateRange = default(DateRange));

        Dictionary<string, int> CountPerUser(DateRange dateRange = default(DateRange));
        Dictionary<string, int> CountBuriedPerUser(DateRange dateRange = default(DateRange));

        Dictionary<string, int> SumVotesDownPerUser(DateRange dateRange = default(DateRange));
        Dictionary<string, int> SumVotesUpPerUser(DateRange dateRange = default(DateRange));

        #endregion
    }
}