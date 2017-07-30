// --------------------------------------------------------------------------------
// <copyright file="QueryableExtensions.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/09/04
// * Time: 1:27 μμ
// --------------------------------------------------------------------------------

using System;
using System.Linq;
using System.Linq.Expressions;
using NGSoftware.Common;
using BuzzStats.Data.NHibernate.Entities;
using NodaTime;

namespace BuzzStats.Data.NHibernate
{
    public static class QueryableExtensions
    {
        public static IQueryable<T> FilterOnCreatedAt<T>(this IQueryable<T> query, DateInterval dateInterval)
            where T : class, IEntity
        {
            DateTime? startDate = dateInterval.Start.ToDateTimeUnspecified();
            DateTime? stopDate = dateInterval.End.ToDateTimeUnspecified();

            if (startDate.HasValue)
            {
                query = query.Where(c => c.CreatedAt >= startDate.Value);
            }

            if (stopDate.HasValue)
            {
                query = query.Where(c => c.CreatedAt < stopDate.Value);
            }

            return query;
        }

        public static IOrderedQueryable<T> OrderBy<T>(
            this IQueryable<T> query,
            string propertyName,
            SortDirection direction,
            bool first)
        {
            Type elementType = typeof(T);
            var property = elementType.GetProperty(propertyName);
            if (property == null)
            {
                throw new ArgumentOutOfRangeException(
                    "propertyName",
                    string.Format("Type {0} does not have a property {1}", elementType, propertyName));
            }

            var typeArgs = new Type[]
            {
                elementType,
                property.PropertyType
            };

            string methodName;
            if (first)
            {
                methodName = direction == SortDirection.Ascending ? "OrderBy" : "OrderByDescending";
            }
            else
            {
                methodName = direction == SortDirection.Ascending ? "ThenBy" : "ThenByDescending";
            }

            var parameter = Expression.Parameter(elementType, "p");
            var propertyAccess = Expression.MakeMemberAccess(parameter, property);
            var orderByExp = Expression.Lambda(propertyAccess, parameter);
            MethodCallExpression resultExp = Expression.Call(
                typeof(Queryable),
                methodName,
                typeArgs,
                query.Expression,
                Expression.Quote(orderByExp));
            return (IOrderedQueryable<T>) query.Provider.CreateQuery<T>(resultExp);
        }

        public static IOrderedQueryable<T> OrderBy<T, TEnum>(this IQueryable<T> query, EnumSortExpression<TEnum> sort)
            where TEnum : struct
        {
            string propertyName = sort.Field.ToString();
            return query.OrderBy<T>(propertyName, sort.Direction, true);
        }

        public static IOrderedQueryable<T> ThenBy<T, TEnum>(this IQueryable<T> query, EnumSortExpression<TEnum> sort)
            where TEnum : struct
        {
            string propertyName = sort.Field.ToString();
            return query.OrderBy<T>(propertyName, sort.Direction, false);
        }
    }
}