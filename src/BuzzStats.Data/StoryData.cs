// --------------------------------------------------------------------------------
// <copyright file="StoryData.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/10/04
// * Time: 10:03 μμ
// --------------------------------------------------------------------------------

using System;
using System.Runtime.Serialization;
using System.Text;

namespace BuzzStats.Data
{
    [DataContract]
    public class StoryData : IEquatable<StoryData>
    {
        public StoryData()
        {
        }

        public StoryData(int storyId)
        {
            StoryId = storyId;
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="StoryData"/> class.
        /// All parameters are required to be not-null in the database.
        /// </summary>
        /// <param name="storyId">
        /// The story id.
        /// </param>
        /// <param name="title">
        /// The title.
        /// </param>
        /// <param name="username">
        /// The username.
        /// </param>
        public StoryData(int storyId, string title, string username)
        {
            StoryId = storyId;
            Title = title;
            Username = username;
        }

        [DataMember]
        public virtual int Category { get; set; }

        [DataMember]
        public virtual DateTime CreatedAt { get; set; }

        [DataMember]
        public virtual DateTime DetectedAt { get; set; }

        [DataMember]
        public virtual string Host { get; set; }

        [DataMember]
        public virtual DateTime LastCheckedAt { get; set; }

        [DataMember]
        public virtual DateTime? LastCommentedAt { get; set; }

        [DataMember]
        public virtual DateTime LastModifiedAt { get; set; }

        [DataMember]
        public virtual DateTime? RemovedAt { get; set; }

        [DataMember]
        public int StoryId { get; set; }

        [DataMember]
        public string Title { get; set; }

        [DataMember]
        public virtual int TotalChecks { get; set; }

        [DataMember]
        public virtual int TotalUpdates { get; set; }

        [DataMember]
        public virtual string Url { get; set; }

        [DataMember]
        public string Username { get; set; }

        [DataMember]
        public virtual int VoteCount { get; set; }

        public static bool IdEquals(StoryData a, StoryData b)
        {
            if (ReferenceEquals(a, b))
            {
                return true;
            }

            return !ReferenceEquals(a, null) && !ReferenceEquals(b, null) && a.StoryId == b.StoryId;
        }

        public virtual object Clone()
        {
            return MemberwiseClone();
        }

        public bool Equals(StoryData other)
        {
            if (ReferenceEquals(other, this))
            {
                return true;
            }

            if (ReferenceEquals(other, null))
            {
                return false;
            }

            return StoryId == other.StoryId
                   && string.Equals(Title, other.Title)
                   && string.Equals(Url, other.Url)
                   && string.Equals(Username, other.Username)
                   && CreatedAt == other.CreatedAt
                   && Category == other.Category
                   && RemovedAt == other.RemovedAt
                   && DetectedAt == other.DetectedAt
                   && LastCheckedAt == other.LastCheckedAt
                   && LastCommentedAt == other.LastCommentedAt
                   && LastModifiedAt == other.LastModifiedAt
                   && TotalChecks == other.TotalChecks
                   && TotalUpdates == other.TotalUpdates
                   && string.Equals(Host, other.Host)
                   && VoteCount == other.VoteCount;
        }

        public override bool Equals(object obj)
        {
            if (ReferenceEquals(obj, this))
            {
                return true;
            }

            if (ReferenceEquals(obj, null))
            {
                return false;
            }

            return Equals(obj as StoryData);
        }

        public override int GetHashCode()
        {
            return StoryId;
        }

        public override string ToString()
        {
            return
                new StringBuilder(GetType().Name).Append(" StoryId=")
                    .Append(StoryId)
                    .Append(" Title=")
                    .Append(Title)
                    .Append(" Url=")
                    .Append(Url)
                    .Append(" Username=")
                    .Append(Username)
                    .Append("\r\n CreatedAt=")
                    .Append(CreatedAt)
                    .Append(" Category=")
                    .Append(Category)
                    .Append(" RemovedAt=")
                    .Append(RemovedAt)
                    .Append("\r\n DetectedAt=")
                    .Append(DetectedAt)
                    .Append(" LastCheckedAt=")
                    .Append(LastCheckedAt)
                    .Append(" LastCommentedAt=")
                    .Append(LastCommentedAt)
                    .Append(" LastModifiedAt=")
                    .Append(LastModifiedAt)
                    .Append("\r\n TotalChecks=")
                    .Append(TotalChecks)
                    .Append(" TotalUpdates=")
                    .Append(TotalUpdates)
                    .Append("\r\n Host=")
                    .Append(Host)
                    .Append(" VoteCount=")
                    .Append(VoteCount)
                    .ToString();
        }
    }
}