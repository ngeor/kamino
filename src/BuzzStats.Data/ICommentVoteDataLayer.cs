// --------------------------------------------------------------------------------
// <copyright file="ICommentVoteDataLayer.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/03/28
// * Time: 12:54:11
// --------------------------------------------------------------------------------

namespace BuzzStats.Data
{
    public interface ICommentVoteDataLayer
    {
        #region CRUD

        void Create(CommentVoteData newCommentVote);
        bool Exists(CommentData ownerComment, int votesUp, int votesDown, bool isBuried);

        #endregion

        #region Querying

        CommentVoteData[] Query(CommentData comment);

        #endregion
    }
}
