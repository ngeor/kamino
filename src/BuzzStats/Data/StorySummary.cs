// --------------------------------------------------------------------------------
// <copyright file="StorySummary.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/10/04
// * Time: 10:13 μμ
// --------------------------------------------------------------------------------

using System;

namespace BuzzStats.Data
{
    public sealed class StorySummary
    {
        public int StoryId { get; set; }
        public string Title { get; set; }
        public string Username { get; set; }
        public int VoteCount { get; set; }

        /// <summary>
        /// Gets or sets the CreatedAt.
        /// </summary>
        public DateTime CreatedAt { get; set; }

        /// <summary>
        /// Gets or sets the LastCheckedAt.
        /// </summary>
        public DateTime LastCheckedAt { get; set; }
    }
}
