// --------------------------------------------------------------------------------
// <copyright file="QueryParameters.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/10/04
// * Time: 10:03 μμ
// --------------------------------------------------------------------------------

using System.Linq;
using NGSoftware.Common;
using NGSoftware.Common.Collections;

namespace BuzzStats.Data
{
    public class QueryParameters<T> where T : struct
    {
        private EnumSortExpression<T>[] _sortBy;

        public int Count { get; set; }
        public int Skip { get; set; }

        public EnumSortExpression<T>[] SortBy
        {
            get
            {
                if (_sortBy == null)
                {
                    _sortBy = new EnumSortExpression<T>[0];
                }

                return _sortBy;
            }

            set { _sortBy = value; }
        }

        public override bool Equals(object obj)
        {
            if (ReferenceEquals(obj, null))
            {
                return false;
            }

            if (ReferenceEquals(obj, this))
            {
                return true;
            }

            QueryParameters<T> that = obj as QueryParameters<T>;
            return that != null
                   && that.Skip == Skip
                   && that.Count == Count
                   && that.SortBy.SequenceEqual(SortBy);
        }

        public override int GetHashCode()
        {
            int result = Skip;
            result = result * 7 + Count;
            foreach (EnumSortExpression<T> sort in SortBy)
            {
                result = result * 11 + sort.GetHashCode();
            }

            return result;
        }

        public override string ToString()
        {
            return string.Format(
                "{0} Skip={1} Count={2} SortBy={3}",
                GetType().Name,
                Skip,
                Count,
                SortBy.ToArrayString());
        }
    }
}