// --------------------------------------------------------------------------------
// <copyright file="CommentDataLayer.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/03/28
// * Time: 12:55:48
// --------------------------------------------------------------------------------

using System;
using System.Collections.Generic;
using System.Linq;
using NHibernate;
using NHibernate.Linq;
using StackExchange.Profiling;
using NGSoftware.Common;
using BuzzStats.Data.NHibernate.Entities;
using NodaTime;

namespace BuzzStats.Data.NHibernate
{
    /// <summary>
    /// Implementation of the Data Layer around an NHibernate ISession.
    /// </summary>
    internal sealed class CommentDataLayer : CoreDataClient, ICommentDataLayer
    {
        public CommentDataLayer(ISession session) : base(session)
        {
        }

        public int Count(DateInterval dateInterval)
        {
            return Session.Query<CommentEntity>().FilterOnCreatedAt(dateInterval).Count();
        }

        public CommentData Create(CommentData newComment)
        {
            if (newComment == null)
            {
                throw new ArgumentNullException("newComment");
            }

            CommentEntity existingComment = CoreData.LoadCommentEntity(newComment.CommentId);
            if (existingComment != null && existingComment.Id > 0)
            {
                throw new PersistentObjectException(string.Format("Comment {0} already exists", newComment.CommentId));
            }

            CommentEntity comment = newComment.ToEntity();
            comment.Story = CoreData.SessionMap(newComment.Story);
            comment.ParentComment = CoreData.SessionMap(newComment.ParentComment, allowNull: true);

            Session.SaveOrUpdate(comment);
            return comment.ToData(newComment.Story);
        }

        public CommentData[] Query(CommentDataQueryParameters queryParameters)
        {
            if (queryParameters == null)
            {
                throw new ArgumentNullException("queryParameters");
            }

            MiniProfiler profiler = MiniProfiler.Current;
            using (profiler.Step("GetComments"))
            {
                StoryEntity storyAlias = null;

                IQueryOver<CommentEntity, CommentEntity> q =
                    Session.QueryOver<CommentEntity>()
                        .JoinAlias(c => c.Story, () => storyAlias)
                        .Where(c => storyAlias.RemovedAt == null);

                int? storyId = queryParameters.StoryId;
                if (storyId.HasValue)
                {
                    q = q.Where(c => storyAlias.StoryId == storyId.Value);
                }

                if (queryParameters.CreatedAt != null)
                {
                    if (queryParameters.CreatedAt.Start > LocalDate.MinIsoValue)
                    {
                        q = q.Where(c => c.CreatedAt >= queryParameters.CreatedAt.Start.ToDateTimeUnspecified());
                    }

                    if (queryParameters.CreatedAt.End < LocalDate.MaxIsoValue)
                    {
                        q = q.Where(c => c.CreatedAt < queryParameters.CreatedAt.End.ToDateTimeUnspecified());
                    }
                }

                if (!string.IsNullOrWhiteSpace(queryParameters.Username))
                {
                    q = q.Where(c => c.Username == queryParameters.Username);
                }

                // TODO: implement with LINQ
                // TODO 2: fix ThenBy sorting
                foreach (EnumSortExpression<CommentSortField> orderExpression in queryParameters.SortBy)
                {
                    switch (orderExpression.Field)
                    {
                        case CommentSortField.CreatedAt:
                            if (orderExpression.Direction == SortDirection.Descending)
                            {
                                q = q.OrderBy(c => c.CreatedAt).Desc;
                            }
                            else
                            {
                                q = q.OrderBy(c => c.CreatedAt).Asc;
                            }

                            break;
                        case CommentSortField.VotesUp:
                            if (orderExpression.Direction == SortDirection.Descending)
                            {
                                q = q.OrderBy(c => c.VotesUp).Desc;
                            }
                            else
                            {
                                q = q.OrderBy(c => c.VotesUp).Asc;
                            }

                            break;
                        default:
                            throw new NotSupportedException(string.Format("Not supported sort field {0}",
                                orderExpression.Field));
                    }
                }

                return q
                    .Skip(queryParameters.Skip)
                    .Take(queryParameters.Count)
                    .List()
                    .ToData(storyMapMode: StoryMapMode.IdTitle);
            }
        }

        public CommentData Read(int commentBusinessId)
        {
            CommentEntity commentEntity = CoreData.LoadCommentEntity(commentBusinessId);
            return commentEntity.ToData();
        }

        public CommentData[] Query(StoryData story, CommentData parentComment)
        {
            StoryEntity storyEntity = CoreData.SessionMap(story);
            CommentEntity parentCommentEntity = CoreData.SessionMap(parentComment, true);
            IOrderedQueryable<CommentEntity> query = from comment in Session.Query<CommentEntity>()
                where
                comment.Story == storyEntity &&
                comment.ParentComment == parentCommentEntity
                orderby comment.CreatedAt
                select comment;

            return query.ToData(story);
        }

        public void Update(CommentData existingComment)
        {
            CommentEntity commentEntity = CoreData.SessionMap(existingComment);

            // existingComment can't be null if we made it up to here
            if (existingComment.CommentId <= 0)
            {
                throw new InvalidCommentIdException();
            }

            Session.SaveOrUpdate(commentEntity);
        }

        public Dictionary<string, int> CountBuriedPerUser(DateInterval dateInterval)
        {
            return Session.Query<CommentEntity>().FilterOnCreatedAt(dateInterval)
                .Where(c => c.IsBuried && c.Story.RemovedAt == null)
                .GroupBy(s => s.Username)
                .Select(g => new
                {
                    g.Key,
                    Count = g.Count()
                }).ToDictionary(k => k.Key, v => v.Count);
        }

        /// <summary>
        /// Gets the total number of comments a user has made in the given time period, per user.
        /// </summary>
        /// <param name="dateInterval">
        /// The date Range.
        /// </param>
        /// <returns>
        /// A dictionary where the keys are user names and the matching values are
        /// the corresponding comment counts.
        /// </returns>
        public Dictionary<string, int> CountPerUser(DateInterval dateInterval)
        {
            return Session.Query<CommentEntity>().FilterOnCreatedAt(dateInterval)
                .Where(c => c.Story.RemovedAt == null)
                .GroupBy(c => c.Username)
                .Select(g => new
                {
                    g.Key,
                    Count = g.Count()
                }).ToDictionary(k => k.Key, v => v.Count);
        }

        public Dictionary<string, int> SumVotesDownPerUser(DateInterval dateInterval)
        {
            return Session
                .Query<CommentEntity>().FilterOnCreatedAt(dateInterval)
                .Where(c => c.Story.RemovedAt == null)
                .GroupBy(c => c.Username)
                .Select(g => new
                {
                    g.Key,
                    Count = g.Sum(c => c.VotesDown)
                }).ToDictionary(k => k.Key, v => v.Count);
        }

        public Dictionary<string, int> SumVotesUpPerUser(DateInterval dateInterval)
        {
            return Session
                .Query<CommentEntity>().FilterOnCreatedAt(dateInterval)
                .Where(c => c.Story.RemovedAt == null)
                .GroupBy(c => c.Username)
                .Select(g => new
                {
                    g.Key,
                    Count = g.Sum(c => c.VotesUp)
                }).ToDictionary(k => k.Key, v => v.Count);
        }
    }
}