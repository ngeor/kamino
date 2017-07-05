// --------------------------------------------------------------------------------
// <copyright file="StoryListingSummary.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/03/22
// * Time: 09:30:37
// --------------------------------------------------------------------------------

using System.Runtime.Serialization;

namespace BuzzStats.Parsing
{
    [DataContract]
    public sealed class StoryListingSummary
    {
        public StoryListingSummary()
        {
        }

        public StoryListingSummary(int storyId)
        {
            StoryId = storyId;
        }

        public StoryListingSummary(int storyId, int? voteCount)
        {
            StoryId = storyId;
            VoteCount = voteCount;
        }

        [DataMember]
        public int StoryId { get; set; }

        [DataMember]
        public int? VoteCount { get; set; }

        public override string ToString()
        {
            return string.Format("[StorySummary: StoryId={0}, VoteCount={1}]", StoryId, VoteCount);
        }
    }
}