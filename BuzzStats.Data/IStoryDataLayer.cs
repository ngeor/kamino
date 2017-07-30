// --------------------------------------------------------------------------------
// <copyright file="IStoryDataLayer.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/03/28
// * Time: 12:54:09
// --------------------------------------------------------------------------------

using System;
using System.Collections.Generic;
using NodaTime;

namespace BuzzStats.Data
{
    public interface IStoryDataLayer
    {
        #region CRUD

        StoryData Create(StoryData newStory);
        StoryData Read(int storyBusinessId);
        void Update(StoryData existingStory);

        #endregion

        #region Querying

        IStoryQuery Query();

        #endregion

        #region Stats

        /// <summary>
        /// Gets the creation date of the oldest story.
        /// </summary>
        /// <returns>The creation date of the oldest story.</returns>
        DateTime OldestStoryDate();

        Dictionary<string, int> GetStoryCountsPerHost(DateInterval dateInterval = default(DateInterval));
        Dictionary<string, int> GetStoryCountsPerUser(DateInterval dateInterval = default(DateInterval));
        Dictionary<string, int> GetCommentedStoryCountsPerUser(DateInterval dateInterval = default(DateInterval));
        MinMaxStats GetMinMaxStats();

        #endregion
    }
}