// --------------------------------------------------------------------------------
// <copyright file="StorySortField.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/10/04
// * Time: 10:03 μμ
// --------------------------------------------------------------------------------

using System;

namespace BuzzStats.Data
{
    [Serializable]
    public enum StorySortField
    {
        LastModifiedAt,
        CreatedAt,
        LastCheckedAt,
        TotalChecks,
        LastCommentedAt,
        StoryId,

        /// <summary>
        /// Not an actual field: LastCheckedAt - LastModifiedAt
        /// </summary>
        ModificationAge
    }
}
