// --------------------------------------------------------------------------------
// <copyright file="StoryPollHistoryData.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/03/29
// * Time: 11:59:03
// --------------------------------------------------------------------------------

using System;

namespace BuzzStats.Data
{
    public class StoryPollHistoryData
    {
        public StoryData Story { get; set; }

        public string SourceId { get; set; }

        public DateTime CheckedAt { get; set; }

        public int HadChanges { get; set; }

        public override string ToString()
        {
            return string.Format(
                "[StoryPollHistoryData: Story={0}, SourceId={1}, CheckedAt={2}, HadChanges={3}]",
                Story,
                SourceId,
                CheckedAt,
                HadChanges);
        }

        public override bool Equals(object obj)
        {
            if (obj == null)
                return false;
            if (ReferenceEquals(this, obj))
                return true;
            if (obj.GetType() != typeof (StoryPollHistoryData))
                return false;
            StoryPollHistoryData other = (StoryPollHistoryData) obj;
            return Story == other.Story &&
                SourceId == other.SourceId &&
                CheckedAt == other.CheckedAt &&
                HadChanges == other.HadChanges;
        }


        public override int GetHashCode()
        {
            unchecked
            {
                return (Story != null ? Story.GetHashCode() : 0) ^ (SourceId != null ? SourceId.GetHashCode() : 0) ^
                    CheckedAt.GetHashCode() ^ HadChanges.GetHashCode();
            }
        }
    }
}
