// --------------------------------------------------------------------------------
// <copyright file="RecentActivity.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/10/04
// * Time: 10:03 μμ
// --------------------------------------------------------------------------------

using System;
using System.Runtime.Serialization;

namespace BuzzStats.Data
{
    /// <summary>
    /// Represents a recent activity on the site.
    /// </summary>
    [DataContract]
    public class RecentActivity : IEquatable<RecentActivity>
    {
        public RecentActivity()
        {
        }

        public RecentActivity(RecentActivity other)
        {
            Age = other.Age;
            CommentId = other.CommentId;
            StoryId = other.StoryId;
            StoryTitle = other.StoryTitle;
            What = other.What;
            Who = other.Who;
            DetectedAtAge = other.DetectedAtAge;
        }

        /// <summary>
        /// Gets or sets the age of this recent activity.
        /// </summary>
        [DataMember]
        public TimeSpan Age { get; set; }

        [DataMember]
        public int CommentId { get; set; }

        [DataMember]
        public int StoryId { get; set; }

        [DataMember]
        public string StoryTitle { get; set; }

        [DataMember]
        public RecentActivityKind What { get; set; }

        [DataMember]
        public string Who { get; set; }

        /// <summary>
        /// Gets or sets the age when the object was detected.
        /// </summary>
        [DataMember]
        public TimeSpan DetectedAtAge { get; set; }

        #region IEquatable implementation

        public bool Equals(RecentActivity other)
        {
            return other != null
                   && string.Equals(Who, other.Who)
                   && What == other.What
                   && Age == other.Age
                   && DetectedAtAge == other.DetectedAtAge
                   && string.Equals(StoryTitle, other.StoryTitle)
                   && StoryId == other.StoryId
                   && CommentId == other.CommentId;
        }

        #endregion

        public override bool Equals(object obj)
        {
            return Equals(obj as RecentActivity);
        }

        public override int GetHashCode()
        {
            int result = 0;

            result += Who != null ? Who.GetHashCode() : 0;
            result = result * 7 + What.GetHashCode();
            result = result * 11 + Age.GetHashCode();
            result = result * 11 + DetectedAtAge.GetHashCode();
            result = result * 13 + (StoryTitle ?? string.Empty).GetHashCode();
            result = result * 17 + StoryId;
            result = result * 23 + CommentId;
            return result;
        }

        public override string ToString()
        {
            return string.Format(
                "[RecentActivity: Who={0}, What={1}, Age={2}, StoryTitle={3}, StoryId={4}, " +
                "CommentId={5}, DetectedAtAge={6}]",
                Who,
                What,
                Age,
                StoryTitle,
                StoryId,
                CommentId,
                DetectedAtAge);
        }
    }
}