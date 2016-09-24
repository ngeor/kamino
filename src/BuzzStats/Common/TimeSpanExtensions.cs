// --------------------------------------------------------------------------------
// <copyright file="TimeSpanExtensions.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/09/04
// * Time: 1:27 μμ
// --------------------------------------------------------------------------------

using System;

namespace BuzzStats.Common
{
    public static class TimeSpanExtensions
    {
        public static string ToAgoString(this TimeSpan ts)
        {
            if (ts.TotalDays >= 1)
            {
                return string.Format(Resources.DaysAgo, (int) ts.TotalDays);
            }
            else if (ts.TotalHours >= 1)
            {
                return string.Format(Resources.HoursAgo, (int) ts.TotalHours);
            }
            else if (ts.TotalMinutes >= 1)
            {
                return string.Format(Resources.MinutesAgo, (int) ts.TotalMinutes);
            }
            else
            {
                return Resources.SecondsAgo;
            }
        }
    }
}
