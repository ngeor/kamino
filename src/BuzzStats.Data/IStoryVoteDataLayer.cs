// --------------------------------------------------------------------------------
// <copyright file="IStoryVoteDataLayer.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/03/28
// * Time: 12:54:14
// --------------------------------------------------------------------------------

using System.Collections.Generic;
using NGSoftware.Common;

namespace BuzzStats.Data
{
    public interface IStoryVoteDataLayer
    {
        #region CRUD

        void Create(StoryVoteData newStoryVote);
        bool Exists(StoryData ownerStory, string voter);

        /// <summary>
        /// Deletes an existing story vote.
        /// </summary>
        /// <param name="story">The story</param>
        /// <param name="voter">The username of the voter.</param>
        void Delete(StoryData story, string voter);

        #endregion

        #region Querying

        StoryVoteData[] Query(int maxCount, string username);
        StoryVoteData[] Query(StoryData story);

        #endregion

        #region Stats

        Dictionary<string, int> SumPerHost(DateRange dateRange = default(DateRange));

        #endregion
    }
}
