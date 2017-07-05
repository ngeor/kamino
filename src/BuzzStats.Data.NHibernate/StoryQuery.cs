// --------------------------------------------------------------------------------
// <copyright file="StoryQuery.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/09/30
// * Time: 8:46 πμ
// --------------------------------------------------------------------------------

using System;
using System.Collections.Generic;
using System.Linq;
using NHibernate.Linq;
using NGSoftware.Common;
using BuzzStats.Data.NHibernate.Entities;

namespace BuzzStats.Data.NHibernate
{
    internal class StoryQuery : IStoryQuery
    {
        private readonly List<EnumSortExpression<StorySortField>> _sortExpressions
            = new List<EnumSortExpression<StorySortField>>();

        private readonly StoryDataLayer _dataLayer;
        private readonly StoryQueryDateFilter _createdAt;
        private readonly StoryQueryDateFilter _lastCheckedAt;
        private readonly StoryQueryDateFilter _lastModifiedAt;

        private int _skip;
        private int _take;
        private string _username;
        private IQueryable<StoryEntity> _query;
        private int[] _excludeIds;

        public StoryQuery(StoryDataLayer dataLayer)
        {
            _dataLayer = dataLayer;
            _createdAt = new StoryQueryDateFilter(this);
            _lastCheckedAt = new StoryQueryDateFilter(this);
            _lastModifiedAt = new StoryQueryDateFilter(this);
        }

        public IStoryQueryDateFilter CreatedAt
        {
            get { return _createdAt; }
        }

        public IStoryQueryDateFilter LastCheckedAt
        {
            get { return _lastCheckedAt; }
        }

        public IStoryQueryDateFilter LastModifiedAt
        {
            get { return _lastModifiedAt; }
        }

        public IEnumerable<StoryData> AsEnumerable()
        {
            PrepareQuery();
            _query = ApplySkipTake(_query);
            return _query.ToData();
        }

        public IEnumerable<int> AsEnumerableOfIds()
        {
            PrepareQuery();
            IQueryable<int> queryOfIds = _query.Select(s => s.StoryId);
            queryOfIds = ApplySkipTake(queryOfIds);
            return queryOfIds.ToArray();
        }

        public int Count()
        {
            PrepareQuery();
            return _query.Count();
        }

        public IStoryQuery Username(string username)
        {
            _username = username;
            return this;
        }

        public IStoryQuery ExcludeIds(IEnumerable<int> storyIds)
        {
            _excludeIds = storyIds.ToArray();
            return this;
        }

        public IStoryQuery OrderBy(EnumSortExpression<StorySortField> sortExpression)
        {
            _sortExpressions.Clear();
            _sortExpressions.Add(sortExpression);
            return this;
        }

        public IStoryQuery ThenBy(EnumSortExpression<StorySortField> sortExpression)
        {
            if (!_sortExpressions.Any())
            {
                throw new InvalidOperationException("You cannot call ThenBy if OrderBy has not been called first.");
            }

            _sortExpressions.Add(sortExpression);
            return this;
        }

        public IStoryQuery Skip(int skip)
        {
            _skip = skip;
            return this;
        }

        public IStoryQuery Take(int count)
        {
            _take = count;
            return this;
        }

        private void PrepareQuery()
        {
            CreateQuery();
            ApplyFilters();
            ApplySort();
        }

        private void CreateQuery()
        {
            _query = _dataLayer.Session.Query<StoryEntity>();
        }

        private void ApplyFilters()
        {
            ExcludeRemoved();
            ExcludeExcludedIds();
            ApplyUsername();
            ExcludeNotCommented();
            ApplyCreatedAt();
            ApplyLastCheckedAt();
            ApplyLastModifiedAt();
        }

        private IOrderedQueryable<StoryEntity> ApplySort(EnumSortExpression<StorySortField> expr)
        {
            if (expr.Field == StorySortField.ModificationAge)
            {
                return _query.OrderBy(s => s.LastCheckedAt - s.LastModifiedAt);
            }

            var orderableQuery = _query.OrderBy(expr);
            return orderableQuery;
        }

        private void ApplySort()
        {
            EnsureSort();

            var orderableQuery = ApplySort(_sortExpressions.First());

            foreach (var storySortExpression in _sortExpressions.Skip(1))
            {
                orderableQuery = orderableQuery.ThenBy(storySortExpression);
            }

            // if all else fails, physical db ID in ascending order
            _query = orderableQuery.ThenBy(s => s.Id);
        }

        private IQueryable<T> ApplySkipTake<T>(IQueryable<T> query)
        {
            if (_skip > 0)
            {
                query = query.Skip(_skip);
            }

            if (_take > 0)
            {
                query = query.Take(_take);
            }

            return query;
        }

        private void ApplyUsername()
        {
            if (!string.IsNullOrWhiteSpace(_username))
            {
                _query = _query.Where(s => s.Username == _username);
            }
        }

        private void ApplyCreatedAt()
        {
            if (_createdAt.DateRange.IsEmpty)
            {
                return;
            }

            DateTime? startDate = _createdAt.DateRange.StartDate;
            DateTime? stopDate = _createdAt.DateRange.StopDate;
            if (startDate.HasValue)
            {
                if (stopDate.HasValue)
                {
                    _query = _query.Where(s => s.CreatedAt >= startDate.Value && s.CreatedAt < stopDate.Value);
                }
                else
                {
                    _query = _query.Where(s => s.CreatedAt >= startDate.Value);
                }
            }
            else
            {
                if (stopDate.HasValue)
                {
                    _query = _query.Where(s => s.CreatedAt < stopDate.Value);
                }
            }
        }

        private void ApplyLastCheckedAt()
        {
            if (_lastCheckedAt.DateRange.IsEmpty)
            {
                return;
            }

            DateTime? startDate = _lastCheckedAt.DateRange.StartDate;
            DateTime? stopDate = _lastCheckedAt.DateRange.StopDate;
            if (startDate.HasValue)
            {
                if (stopDate.HasValue)
                {
                    _query = _query.Where(s => s.LastCheckedAt >= startDate.Value && s.LastCheckedAt < stopDate.Value);
                }
                else
                {
                    _query = _query.Where(s => s.LastCheckedAt >= startDate.Value);
                }
            }
            else
            {
                if (stopDate.HasValue)
                {
                    _query = _query.Where(s => s.LastCheckedAt < stopDate.Value);
                }
            }
        }

        private void ApplyLastModifiedAt()
        {
            if (_lastModifiedAt.DateRange.IsEmpty)
            {
                return;
            }

            DateTime? startDate = _lastModifiedAt.DateRange.StartDate;
            DateTime? stopDate = _lastModifiedAt.DateRange.StopDate;
            if (startDate.HasValue)
            {
                if (stopDate.HasValue)
                {
                    _query = _query.Where(
                        s => s.LastModifiedAt >= startDate.Value && s.LastModifiedAt < stopDate.Value);
                }
                else
                {
                    _query = _query.Where(s => s.LastModifiedAt >= startDate.Value);
                }
            }
            else
            {
                if (stopDate.HasValue)
                {
                    _query = _query.Where(s => s.LastModifiedAt < stopDate.Value);
                }
            }
        }

        private void ExcludeNotCommented()
        {
            if (_sortExpressions.Any(s => s.Field == StorySortField.LastCommentedAt))
            {
                _query = _query.Where(s => s.LastCommentedAt != null);
            }
        }

        private void ExcludeExcludedIds()
        {
            if (_excludeIds == null || _excludeIds.Length <= 0)
            {
                return;
            }

            _query = _query.Where(s => !_excludeIds.Contains(s.StoryId));
        }

        private void ExcludeRemoved()
        {
            _query = _query.Where(s => s.RemovedAt == null);
        }

        /// <summary>
        /// Ensures that we have a valid sort expression.
        /// If the parameter is empty, the default fallback (CreatedAt DESC) will be returned.
        /// </summary>
        private void EnsureSort()
        {
            if (NeedsSortFallback())
            {
                _sortExpressions.Add(StorySortField.CreatedAt.Desc());
            }
        }

        private bool NeedsSortFallback()
        {
            if (_sortExpressions.Count <= 0)
            {
                // no sort defined, we need a sort then...
                return true;
            }

            // get the last expression on the list
            var lastSortExpression = _sortExpressions[_sortExpressions.Count - 1];

            // if it is not equal to 'CreatedAt DESC', then we'll add it ourselves
            return !lastSortExpression.Equals(StorySortField.CreatedAt.Desc());
        }
    }
}