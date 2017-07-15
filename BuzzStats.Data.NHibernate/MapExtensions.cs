// --------------------------------------------------------------------------------
// <copyright file="MapExtensions.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/06/24
// * Time: 3:08 μμ
// --------------------------------------------------------------------------------

using System.Collections.Generic;
using System.Linq;
using NGSoftware.Common;
using BuzzStats.Data.NHibernate.Entities;

namespace BuzzStats.Data.NHibernate
{
    /// <summary>
    /// Extension methods to easily convert between entity (internal) and data (external) classes.
    /// </summary>
    public static class MapExtensions
    {
        #region Story

        public static StoryData ToData(this StoryEntity storyEntity)
        {
            return storyEntity == null
                ? null
                : new StoryData
                {
                    Category = storyEntity.Category,
                    CreatedAt = storyEntity.CreatedAt,
                    DetectedAt = storyEntity.DetectedAt,
                    Host = storyEntity.Host,
                    LastCheckedAt = storyEntity.LastCheckedAt,
                    LastCommentedAt = storyEntity.LastCommentedAt,
                    LastModifiedAt = storyEntity.LastModifiedAt,
                    RemovedAt = storyEntity.RemovedAt,
                    StoryId = storyEntity.StoryId,
                    Title = storyEntity.Title,
                    TotalChecks = storyEntity.TotalChecks,
                    TotalUpdates = storyEntity.TotalUpdates,
                    Url = storyEntity.Url,
                    Username = storyEntity.Username,
                    VoteCount = storyEntity.VoteCount
                };
        }

        public static StoryData[] ToData(this IEnumerable<StoryEntity> storyEntities)
        {
            return storyEntities.ToArray().Select(e => e.ToData()).ToArray();
        }

        public static StoryEntity ToEntity(this StoryData storyData, StoryEntity target)
        {
            target.Category = storyData.Category;
            target.CreatedAt = storyData.CreatedAt;
            target.DetectedAt = storyData.DetectedAt;
            target.Host = storyData.Host;
            target.LastCheckedAt = storyData.LastCheckedAt;
            target.LastCommentedAt = storyData.LastCommentedAt;
            target.LastModifiedAt = storyData.LastModifiedAt;
            target.RemovedAt = storyData.RemovedAt;
            target.StoryId = storyData.StoryId;
            target.Title = storyData.Title;
            target.TotalChecks = storyData.TotalChecks;
            target.TotalUpdates = storyData.TotalUpdates;
            target.Url = storyData.Url;
            target.Username = storyData.Username;
            target.VoteCount = storyData.VoteCount;
            return target;
        }

        public static StoryEntity ToEntity(this StoryData storyData)
        {
            return storyData.ToEntity(new StoryEntity());
        }

        public static StoryEntity IdMap(this StoryData storyData)
        {
            return storyData == null ? null : new StoryEntity {StoryId = storyData.StoryId};
        }

        public static StoryData IdMap(this StoryEntity storyEntity)
        {
            return storyEntity == null ? null : new StoryData {StoryId = storyEntity.StoryId};
        }

        public static StoryData IdTitleMap(this StoryEntity storyEntity)
        {
            return storyEntity == null
                ? null
                : new StoryData
                {
                    StoryId = storyEntity.StoryId,
                    Title = storyEntity.Title
                };
        }

        #endregion

        #region StoryVote

        public static StoryVoteData ToData(this StoryVoteEntity storyVoteEntity, StoryData ownerStory = null)
        {
            return storyVoteEntity == null
                ? null
                : new StoryVoteData
                {
                    CreatedAt = storyVoteEntity.CreatedAt,
                    Username = storyVoteEntity.Username,
                    Story = ownerStory != null ? ownerStory : storyVoteEntity.Story.IdMap()
                };
        }

        public static StoryVoteData[] ToData(
            this IEnumerable<StoryVoteEntity> storyVoteEntities,
            StoryData ownerStory = null)
        {
            // first .ToArray() makes sure the NHibernate query has completed
            return storyVoteEntities.ToArray().Select(e => e.ToData(ownerStory)).ToArray();
        }

        public static StoryVoteEntity ToEntity(this StoryVoteData storyVoteData)
        {
            return new StoryVoteEntity
            {
                CreatedAt = storyVoteData.CreatedAt,
                Username = storyVoteData.Username
            };
        }

        #endregion

        #region Comment

        public static CommentData ToData(
            this CommentEntity commentEntity,
            StoryData story = null,
            StoryMapMode storyMapMode = StoryMapMode.IdOnly)
        {
            if (commentEntity == null)
            {
                return null;
            }

            if (story == null)
            {
                story = storyMapMode == StoryMapMode.IdOnly
                    ? commentEntity.Story.IdMap()
                    : commentEntity.Story.IdTitleMap();
            }

            return new CommentData
            {
                CommentId = commentEntity.CommentId,
                CreatedAt = commentEntity.CreatedAt,
                DetectedAt = commentEntity.DetectedAt,
                IsBuried = commentEntity.IsBuried,
                ParentComment = commentEntity.ParentComment.IdMap(),
                Story = story,
                Username = commentEntity.Username,
                VotesDown = commentEntity.VotesDown,
                VotesUp = commentEntity.VotesUp
            };
        }

        public static CommentData[] ToData(
            this IEnumerable<CommentEntity> commentEntities,
            StoryData story = null,
            StoryMapMode storyMapMode = StoryMapMode.IdOnly)
        {
            return commentEntities.ToArray().Select(e => e.ToData(story, storyMapMode)).ToArray();
        }

        public static CommentEntity ToEntity(this CommentData commentData, CommentEntity target)
        {
            target.CommentId = commentData.CommentId;
            target.CreatedAt = commentData.CreatedAt;
            target.DetectedAt = commentData.DetectedAt;
            target.IsBuried = commentData.IsBuried;
            // Do not map ParentComment and Story because NHibernate has already load them
            target.Username = commentData.Username;
            target.VotesDown = commentData.VotesDown;
            target.VotesUp = commentData.VotesUp;
            return target;
        }

        public static CommentEntity ToEntity(this CommentData commentData)
        {
            return commentData.ToEntity(new CommentEntity());
        }

        public static CommentData IdMap(this CommentEntity commentEntity)
        {
            return commentEntity == null ? null : new CommentData {CommentId = commentEntity.CommentId};
        }

        public static CommentEntity IdMap(this CommentData commentData)
        {
            return commentData == null ? null : new CommentEntity {CommentId = commentData.CommentId};
        }

        #endregion

        #region CommentVote

        public static CommentVoteData ToData(this CommentVoteEntity commentVoteEntity)
        {
            return commentVoteEntity == null
                ? null
                : new CommentVoteData
                {
                    Comment = commentVoteEntity.Comment.IdMap(),
                    CreatedAt = commentVoteEntity.CreatedAt,
                    IsBuried = commentVoteEntity.IsBuried,
                    VotesDown = commentVoteEntity.VotesDown,
                    VotesUp = commentVoteEntity.VotesUp
                };
        }

        public static CommentVoteData[] ToData(this IEnumerable<CommentVoteEntity> commentVoteEntities)
        {
            return commentVoteEntities.ToArray().Select(e => e.ToData()).ToArray();
        }

        public static CommentVoteEntity ToEntity(this CommentVoteData commentVoteData)
        {
            return new CommentVoteEntity
            {
                CreatedAt = commentVoteData.CreatedAt,
                IsBuried = commentVoteData.IsBuried,
                VotesDown = commentVoteData.VotesDown,
                VotesUp = commentVoteData.VotesUp,
                Comment = null
            };
        }

        #endregion

        #region RecentActivity

        public static RecentActivity ToData(this RecentActivityEntity recentActivityEntity)
        {
            return new RecentActivity
            {
                Age = recentActivityEntity.CreatedAt.Age(),
                CommentId = recentActivityEntity.CommentId.GetValueOrDefault(),
                DetectedAtAge = recentActivityEntity.DetectedAt.Age(),
                StoryId = recentActivityEntity.StoryId,
                StoryTitle = recentActivityEntity.Title,
                What = (RecentActivityKind) recentActivityEntity.What,
                Who = recentActivityEntity.Username
            };
        }

        #endregion

        #region WebPage

        public static WebPageData ToData(this WebPageEntity webPage)
        {
            return new WebPageData
            {
                Url = webPage.Url,
                Plugin = webPage.Plugin
            };
        }

        public static WebPageEntity ToEntity(this WebPageData webPage)
        {
            return new WebPageEntity
            {
                Url = webPage.Url,
                Plugin = webPage.Plugin
            };
        }

        #endregion

        #region StoryPollHistory

        public static StoryPollHistoryData ToData(this StoryPollHistoryEntity storyPollHistory)
        {
            return new StoryPollHistoryData
            {
                Story = storyPollHistory.Story.ToData(),
                HadChanges = storyPollHistory.HadChanges,
                SourceId = storyPollHistory.SourceId,
                CheckedAt = storyPollHistory.CheckedAt
            };
        }

        #endregion
    }
}