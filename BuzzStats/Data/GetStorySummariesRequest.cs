using System;
using System.Linq;
using NGSoftware.Common;
using NGSoftware.Common.Collections;

namespace BuzzStats.Data
{
    [Serializable]
    public class GetStorySummariesRequest : IEquatable<GetStorySummariesRequest>
    {
        public GetStorySummariesRequest()
        {
            SortBy = new EnumSortExpression<StorySortField>[0];
        }

        public GetStorySummariesRequest(EnumSortExpression<StorySortField> sortBy, int rowIndex = 0, int maxRows = 0)
        {
            SortBy = new[] {sortBy};
            RowIndex = rowIndex;
            MaxRows = maxRows;
        }

        public int RowIndex { get; set; }

        public int MaxRows { get; set; }

        public EnumSortExpression<StorySortField>[] SortBy { get; set; }

        public override string ToString()
        {
            return string.Format("RowIndex: {0}, MaxRows: {1}, SortBy: {2}", RowIndex, MaxRows, SortBy.ToArrayString());
        }

        public bool Equals(GetStorySummariesRequest other)
        {
            if (ReferenceEquals(null, other)) return false;
            if (ReferenceEquals(this, other)) return true;
            return RowIndex == other.RowIndex && MaxRows == other.MaxRows && SortBy.SequenceEqual(other.SortBy);
        }

        public override bool Equals(object obj)
        {
            if (ReferenceEquals(null, obj)) return false;
            if (ReferenceEquals(this, obj)) return true;
            if (obj.GetType() != GetType()) return false;
            return Equals((GetStorySummariesRequest) obj);
        }

        public override int GetHashCode()
        {
            unchecked
            {
                int hashCode = RowIndex;
                hashCode = (hashCode * 397) ^ MaxRows;
                hashCode = SortBy.Aggregate(hashCode,
                    (current, sortExpression) => current * 13 + sortExpression.GetHashCode());
                return hashCode;
            }
        }
    }
}