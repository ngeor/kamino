// --------------------------------------------------------------------------------
// <copyright file="CommentVoteData.cs" company="Nikolaos Georgiou">
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
    public class CommentVoteData : IEquatable<CommentVoteData>
    {
        public DateTime CreatedAt { get; set; }

        public bool IsBuried { get; set; }

        public int VotesDown { get; set; }

        public int VotesUp { get; set; }

        public CommentData Comment { get; set; }

        protected int CommentId
        {
            get { return Comment != null ? Comment.CommentId : -1; }
        }

        public bool Equals(CommentVoteData other)
        {
            if (ReferenceEquals(other, null))
            {
                return false;
            }

            if (ReferenceEquals(other, this))
            {
                return true;
            }

            return CreatedAt == other.CreatedAt
                   && IsBuried.Equals(other.IsBuried)
                   && VotesDown == other.VotesDown
                   && VotesUp == other.VotesUp
                   && CommentData.IdEquals(Comment, other.Comment);
        }

        public override bool Equals(object obj)
        {
            return Equals(obj as CommentVoteData);
        }

        public override int GetHashCode()
        {
            int result = CommentId;
            result = result * 7 + CreatedAt.GetHashCode();
            result = result * 11 + VotesUp;
            result = result * 13 + VotesDown;
            result = result * 17 + IsBuried.GetHashCode();
            return result;
        }

        public override string ToString()
        {
            return string.Format(
                "{0}, CreatedAt: {1}, VotesUp: {2}, VotesDown: {3}, IsBuried: {4}, CommentId={5}",
                GetType().Name,
                CreatedAt,
                VotesUp,
                VotesDown,
                IsBuried,
                CommentId);
        }
    }
}