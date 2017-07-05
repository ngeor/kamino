// --------------------------------------------------------------------------------
// <copyright file="CountStatsRequest.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/09/04
// * Time: 1:27 μμ
// --------------------------------------------------------------------------------

using System;
using NGSoftware.Common;

namespace BuzzStats.Data
{
    public class CountStatsRequest : IEquatable<CountStatsRequest>
    {
        public CountStatsRequest()
        {
        }

        public DateTimeUnit Interval { get; set; }

        public DateTime? Start { get; set; }

        public DateTime? Stop { get; set; }

        public DateRange DateRange
        {
            get { return DateRange.Create(Start, Stop); }
        }

        public bool Equals(CountStatsRequest other)
        {
            if (ReferenceEquals(null, other)) return false;
            if (ReferenceEquals(this, other)) return true;
            return Interval == other.Interval && Start.Equals(other.Start) && Stop.Equals(other.Stop);
        }

        public override bool Equals(object obj)
        {
            if (ReferenceEquals(null, obj)) return false;
            if (ReferenceEquals(this, obj)) return true;
            if (obj.GetType() != GetType()) return false;
            return Equals((CountStatsRequest) obj);
        }

        public override int GetHashCode()
        {
            unchecked
            {
                int hashCode = (int) Interval;
                hashCode = (hashCode * 397) ^ Start.GetHashCode();
                hashCode = (hashCode * 397) ^ Stop.GetHashCode();
                return hashCode;
            }
        }
    }
}