// --------------------------------------------------------------------------------
// <copyright file="CommentData.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/10/04
// * Time: 10:03 μμ
// --------------------------------------------------------------------------------

using System;
using System.Xml.Serialization;

namespace BuzzStats.Data
{
    [Serializable]
    public class CommentData : IEquatable<CommentData>
    {
        public CommentData()
        {
        }

        public CommentData(int commentId)
        {
            CommentId = commentId;
        }

        public int CommentId { get; set; }

        public DateTime CreatedAt { get; set; }

        public DateTime DetectedAt { get; set; }

        public bool IsBuried { get; set; }

        /// <summary>
        /// Gets or sets the parent comment.
        /// </summary>
        /// <remarks>
        /// This member is excluded from serialization to avoid
        /// having it in the web service responses.
        /// </remarks>
        [XmlIgnore]
        public CommentData ParentComment { get; set; }

        public int ParentCommentId
        {
            get { return (ParentComment != null) ? ParentComment.CommentId : -1; }
        }

        public StoryData Story { get; set; }

        public int StoryId
        {
            get { return (Story != null) ? Story.StoryId : -1; }
        }

        public string Username { get; set; }

        public int VotesDown { get; set; }

        public int VotesUp { get; set; }

        public static bool IdEquals(CommentData a, CommentData b)
        {
            if (ReferenceEquals(a, b))
            {
                return true;
            }

            return a != null && a.CommentId == b.CommentId;
        }

        public object Clone()
        {
            return MemberwiseClone();
        }

        public bool Equals(CommentData other)
        {
            if (ReferenceEquals(null, other))
            {
                return false;
            }

            if (ReferenceEquals(this, other))
            {
                return true;
            }

            return CommentId == other.CommentId
                   && CreatedAt == other.CreatedAt
                   && DetectedAt == other.DetectedAt
                   && IsBuried.Equals(other.IsBuried)
                   && string.Equals(Username, other.Username)
                   && VotesDown == other.VotesDown
                   && VotesUp == other.VotesUp
                   && StoryData.IdEquals(Story, other.Story) && IdEquals(ParentComment, other.ParentComment);
        }

        public override bool Equals(object obj)
        {
            if (ReferenceEquals(null, obj))
            {
                return false;
            }

            return ReferenceEquals(this, obj) || Equals(obj as CommentData);
        }

        public override int GetHashCode()
        {
            return CommentId;
        }

        public override string ToString()
        {
            return string.Format(
                "CommentId: {0}, CreatedAt: {1}, DetectedAt: {2}, IsBuried: {3}, Username: {4}, VotesDown: {5}, "
                + "VotesUp: {6}, StoryId: {7}, ParentCommentId: {8}",
                CommentId,
                CreatedAt,
                DetectedAt,
                IsBuried,
                Username,
                VotesDown,
                VotesUp,
                StoryId,
                ParentCommentId);
        }
    }
}