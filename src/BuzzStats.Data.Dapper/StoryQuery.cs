using System;
using System.Collections.Generic;
using System.Data.Common;
using System.Linq;
using Dapper;
using NGSoftware.Common;
using NGSoftware.Common.Collections;

namespace BuzzStats.Data.Dapper
{
    class StoryQuery : IStoryQuery
    {
        private readonly DbConnection _connection;
        private int[] _excludeIds;
        private EnumSortExpression<StorySortField> _orderBy;

        public StoryQuery(DbConnection connection)
        {
            _connection = connection;
        }

        public IStoryQueryDateFilter CreatedAt
        {
            get { throw new NotImplementedException(); }
        }

        public IStoryQueryDateFilter LastCheckedAt
        {
            get { throw new NotImplementedException(); }
        }

        public IStoryQueryDateFilter LastModifiedAt
        {
            get { throw new NotImplementedException(); }
        }

        public IEnumerable<StoryData> AsEnumerable()
        {
            throw new NotImplementedException();
        }

        public IEnumerable<int> AsEnumerableOfIds()
        {
            var sql = "SELECT StoryId FROM Story WHERE RemovedAt IS NULL";
            if (_orderBy != null)
            {
                sql += " ORDER BY ";
                switch (_orderBy.Field)
                {
                    case StorySortField.ModificationAge:
                        sql += "(LastCheckedAt-LastModifiedAt)";
                        break;
                    default:
                        sql += _orderBy.Field.ToString();
                        break;
                }

                if (_orderBy.Direction == SortDirection.Descending)
                {
                    sql += " DESC";
                }
            }
            return _connection.Query<int>(sql);
        }

        public int Count()
        {
            if (_excludeIds != null && _excludeIds.Any())
            {
                return
                    _connection.ExecuteScalar<int>(
                        "SELECT COUNT(*) FROM Story WHERE RemovedAt IS NULL AND StoryId NOT IN " +
                        _excludeIds.ToArrayString().Replace('[', '(').Replace(']', ')'));
            }

            return _connection.Query<int>(
                "SELECT COUNT(*) FROM Story " +
                "WHERE RemovedAt IS NULL").FirstOrDefault();
        }

        public IStoryQuery ExcludeIds(IEnumerable<int> storyIds)
        {
            _excludeIds = storyIds.ToArray();
            return this;
        }

        public IStoryQuery OrderBy(EnumSortExpression<StorySortField> sortExpression)
        {
            _orderBy = sortExpression;
            return this;
        }

        public IStoryQuery Skip(int skip)
        {
            throw new NotImplementedException();
        }

        public IStoryQuery Take(int count)
        {
            throw new NotImplementedException();
        }

        public IStoryQuery ThenBy(EnumSortExpression<StorySortField> sortExpression)
        {
            throw new NotImplementedException();
        }

        public IStoryQuery Username(string username)
        {
            throw new NotImplementedException();
        }
    }
}