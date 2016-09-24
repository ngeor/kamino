// --------------------------------------------------------------------------------
// <copyright file="UpdateResult.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/09/04
// * Time: 1:27 μμ
// --------------------------------------------------------------------------------

using System;

namespace BuzzStats.Persister
{
    [Flags]
    [Serializable]
    public enum UpdateResult
    {
        NoChanges = 0,
        Created = 1,
        NewVotes = 2,
        NewComments = 4,
        NewCommentVotes = 8,
        Removed = 16,
        LessVotes = 32
    }
}
