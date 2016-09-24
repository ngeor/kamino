// --------------------------------------------------------------------------------
// <copyright file="StoryVoteData.cs" company="Nikolaos Georgiou">
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
    public class StoryVoteData : IEquatable<StoryVoteData>
    {
        public StoryData Story { get; set; }

        public DateTime CreatedAt { get; set; }

        public string Username { get; set; }

        public int StoryId
        {
            get { return Story != null ? Story.StoryId : -1; }
        }

        public bool Equals(StoryVoteData other)
        {
            if (ReferenceEquals(other, null))
            {
                return false;
            }

            if (ReferenceEquals(other, this))
            {
                return true;
            }

            return string.Equals(Username, other.Username)
                && CreatedAt == other.CreatedAt
                && StoryData.IdEquals(Story, other.Story);
        }

        public override bool Equals(object obj)
        {
            return Equals(obj as StoryVoteData);
        }

        public override string ToString()
        {
            return string.Format(
                "{0} Username: {1}, CreatedAt: {2}, StoryId: {3}",
                GetType().Name,
                Username,
                CreatedAt,
                StoryId);
        }

        public override int GetHashCode()
        {
            int result = StoryId;
            result = result*7 + CreatedAt.GetHashCode();
            result = result*11 + (Username != null ? Username.GetHashCode() : 0);
            return result;
        }
    }
}
