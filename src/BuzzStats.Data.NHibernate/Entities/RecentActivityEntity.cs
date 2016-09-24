//
//  RecentActivityEntity.cs
//
//  Author:
//       ngeor
//
//  Copyright (c) 2014 ngeor

using System;

namespace BuzzStats.Data.NHibernate.Entities
{
    public class RecentActivityEntity
    {
        public virtual int StoryId { get; set; }

        public virtual int? CommentId { get; set; }

        public virtual string Title { get; set; }

        public virtual string Username { get; set; }

        public virtual DateTime CreatedAt { get; set; }

        public virtual DateTime DetectedAt { get; set; }

        public virtual int What { get; set; }

        public override string ToString()
        {
            return string.Format(
                "[RecentActivityEntity: StoryId={0}, CommentId={1}, Title={2}, Username={3}, " +
                    "CreatedAt={4}, DetectedAt={5}, What={6}]",
                StoryId,
                CommentId,
                Title,
                Username,
                CreatedAt,
                DetectedAt,
                What);
        }
    }
}
