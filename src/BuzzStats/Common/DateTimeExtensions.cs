// --------------------------------------------------------------------------------
// <copyright file="DateTimeExtensions.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/09/04
// * Time: 1:27 μμ
// --------------------------------------------------------------------------------

using System;

namespace BuzzStats.Common
{
    public static class DateTimeExtensions
    {
        public static string ToAgoString(this DateTime dt)
        {
            TimeSpan ts = DateTime.UtcNow.Subtract(dt);
            return ts.ToAgoString();
        }
    }
}
