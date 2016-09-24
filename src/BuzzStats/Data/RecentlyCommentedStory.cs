// --------------------------------------------------------------------------------
// <copyright file="RecentlyCommentedStory.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/09/30
// * Time: 8:32 πμ
// --------------------------------------------------------------------------------

using System;
using System.Linq;
using NGSoftware.Common.Collections;
using BuzzStats.Common;

namespace BuzzStats.Data
{
    public sealed class RecentlyCommentedStory : IEquatable<RecentlyCommentedStory>
    {
        private Comment[] _comments;

        public int StoryId { get; set; }

        public string Title { get; set; }

        public Comment[] Comments
        {
            get { return _comments; }
            set
            {
                _comments = value;
                AdoptComments();
            }
        }

        public bool Equals(RecentlyCommentedStory other)
        {
            return other != null
                && StoryId == other.StoryId
                && string.Equals(Title, other.Title)
                && ((Comments != null && other.Comments != null && Comments.SequenceEqual(other.Comments)) ||
                    (Comments == null && other.Comments == null));
        }

        public override string ToString()
        {
            return string.Format(
                "{0} StoryId={1} Title={2} Comments={3}",
                GetType().Name,
                StoryId,
                Title,
                Comments.ToArrayString());
        }

        /// <summary>
        /// Fixes the story id in the contained comments.
        /// </summary>
        private void AdoptComments()
        {
            foreach (Comment comment in _comments ?? Enumerable.Empty<Comment>())
            {
                comment.StoryId = StoryId;
            }
        }

        public sealed class Comment : IEquatable<Comment>
        {
            public TimeSpan Age { get; set; }
            public int CommentId { get; set; }
            public string Username { get; set; }
            public int VotesUp { get; set; }

            internal int StoryId { get; set; }

            public string StoryUrl
            {
                get { return UrlProvider.StoryUrl(StoryId, CommentId); }
            }

            public bool Equals(Comment other)
            {
                return other != null
                    && CommentId == other.CommentId
                    && VotesUp == other.VotesUp
                    && string.Equals(Username, other.Username)
                    && Age == other.Age;
            }

            public override string ToString()
            {
                return string.Format(
                    "{0} CommentId={1} Username={2} VotesUp={3} Age={4}",
                    GetType().Name,
                    CommentId,
                    Username,
                    VotesUp,
                    Age);
            }
        }
    }
}
