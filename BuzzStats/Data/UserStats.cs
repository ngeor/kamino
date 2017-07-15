// --------------------------------------------------------------------------------
// <copyright file="UserStats.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/09/04
// * Time: 1:27 μμ
// --------------------------------------------------------------------------------

using System;

namespace BuzzStats.Data
{
    public class UserStats : IEquatable<UserStats>
    {
        public double BuriedCommentCount { get; set; }
        public double CommentCount { get; set; }
        public double CommentedStoriesCount { get; set; }
        public double StoryCount { get; set; }
        public string Username { get; set; }

        public double VotesDiff
        {
            get { return VotesUp - VotesDown; }
        }

        public double VotesDiffByCommentCount
        {
            get { return CommentCount > 0 ? VotesDiff / CommentCount : 0; }
        }

        public double VotesDown { get; set; }
        public double VotesUp { get; set; }

        #region IEquatable implementation

        public bool Equals(UserStats other)
        {
            return other != null
                   && string.Equals(Username, other.Username)
                   && StoryCount == other.StoryCount
                   && CommentCount == other.CommentCount
                   && VotesUp == other.VotesUp
                   && VotesDown == other.VotesDown
                   && BuriedCommentCount == other.BuriedCommentCount
                   && CommentedStoriesCount == other.CommentedStoriesCount;
        }

        #endregion

        public override bool Equals(object obj)
        {
            return Equals(obj as UserStats);
        }

        public override int GetHashCode()
        {
            double result = 0;
            result = (Username ?? string.Empty).GetHashCode();
            result = result * 7 + StoryCount;
            result = result * 11 + CommentCount;
            result = result * 13 + VotesUp;
            result = result * 17 + VotesDown;
            result = result * 19 + BuriedCommentCount;
            result = result * 23 + CommentedStoriesCount;
            return (int) result;
        }

        public override string ToString()
        {
            return string.Format(
                "[UserStats: Username={0}, StoryCount={1}, CommentCount={2}, " +
                "CommentVotesUpSum={3}, CommentVotesDownSum={4}, BuriedCommentsCount={5}, CommentedStoriesCount={6}]",
                Username, StoryCount, CommentCount, VotesUp, VotesDown, BuriedCommentCount, CommentedStoriesCount);
        }
    }
}