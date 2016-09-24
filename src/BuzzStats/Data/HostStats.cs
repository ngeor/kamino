// --------------------------------------------------------------------------------
// <copyright file="HostStats.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/10/04
// * Time: 10:03 μμ
// --------------------------------------------------------------------------------

using System;

namespace BuzzStats.Data
{
    /// <summary>
    /// Host stats.
    /// </summary>
    public class HostStats : IEquatable<HostStats>
    {
        public string Host { get; set; }
        public int StoryCount { get; set; }
        public int VoteCount { get; set; }

        public double VoteStoryRatio
        {
            get { return StoryCount <= 0 ? -1 : VoteCount/(double) StoryCount; }
        }

        public bool Equals(HostStats other)
        {
            return other != null
                && string.Equals(Host, other.Host)
                && StoryCount == other.StoryCount
                && VoteCount == other.VoteCount;
        }

        public override bool Equals(object other)
        {
            return Equals(other as HostStats);
        }

        public override int GetHashCode()
        {
            int result = Host != null ? Host.GetHashCode() : 0;
            result = result*7 + StoryCount;
            result = result*13 + VoteCount;
            return result;
        }

        public override string ToString()
        {
            return string.Format("[HostStats Host={0} StoryCount={1} VoteCount={2}", Host, StoryCount, VoteCount);
        }
    }
}
