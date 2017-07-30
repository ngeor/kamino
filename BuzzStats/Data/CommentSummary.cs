// --------------------------------------------------------------------------------
// <copyright file="CommentSummary.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/10/04
// * Time: 10:13 μμ
// --------------------------------------------------------------------------------

using System;
using BuzzStats.Common;

namespace BuzzStats.Data
{
    public sealed class CommentSummary
    {
        public TimeSpan Age { get; set; }
        public int CommentId { get; set; }
        public string Username { get; set; }
        public int VotesUp { get; set; }

        /// <summary>
        /// Gets or sets the Story.
        /// </summary>
        public ParentStory Story { get; set; }

        public override string ToString()
        {
            return string.Format(
                "{0} CommentId={1} Username={2} VotesUp={3} Age={4} Story={5}",
                GetType().Name,
                CommentId,
                Username,
                VotesUp,
                Age,
                Story);
        }

        public override int GetHashCode()
        {
            int result = CommentId;
            result = result * 7 + (Username ?? string.Empty).GetHashCode();
            result = result * 11 + VotesUp;
            result = result * 13 + Age.GetHashCode();
            result = result * 17 + (Story != null ? Story.GetHashCode() : 0);
            return result;
        }

        public override bool Equals(object obj)
        {
            CommentSummary that = obj as CommentSummary;
            return that != null
                   && CommentId == that.CommentId
                   && string.Equals(Username, that.Username)
                   && VotesUp == that.VotesUp
                   && Age == that.Age
                   && ((Story == null && that.Story == null) || (Story != null && Story.Equals(that.Story)));
        }

        public sealed class ParentStory
        {
            /// <summary>
            /// Gets or sets the StoryId.
            /// </summary>
            public int StoryId { get; set; }

            /// <summary>
            /// Gets or sets the Title.
            /// </summary>
            public string Title { get; set; }

            public override string ToString()
            {
                return string.Format("StoryId={0} Title={1}", StoryId, Title);
            }

            public override int GetHashCode()
            {
                return StoryId ^ (Title ?? string.Empty).GetHashCode();
            }

            public override bool Equals(object obj)
            {
                ParentStory that = obj as ParentStory;
                return that != null && StoryId == that.StoryId && string.Equals(Title, that.Title);
            }
        }
    }
}