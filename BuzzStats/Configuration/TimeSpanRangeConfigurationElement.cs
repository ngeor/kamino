// --------------------------------------------------------------------------------
// <copyright file="TimeSpanRangeConfigurationElement.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/03/29
// * Time: 08:59:24
// --------------------------------------------------------------------------------

using System;
using System.Configuration;
using NGSoftware.Common;

namespace BuzzStats.Configuration
{
    /// <summary>
    /// Represents a time span range. Has minimum and maximum range, both optional.
    /// </summary>
    public class TimeSpanRangeConfigurationElement : ConfigurationElement
    {
        [ConfigurationProperty(PropertyNames.Min, IsRequired = false, DefaultValue = "00:00:00")]
        [TimeSpanValidator(MinValueString = "00:00:00", MaxValueString = "23:59:59", ExcludeRange = false)]
        public TimeSpan Min
        {
            get { return (TimeSpan) this[PropertyNames.Min]; }
            set { this[PropertyNames.Min] = value; }
        }

        [ConfigurationProperty(PropertyNames.Max, IsRequired = false, DefaultValue = "00:00:00")]
        [TimeSpanValidator(MinValueString = "00:00:00", MaxValueString = "23:59:59", ExcludeRange = false)]
        public TimeSpan Max
        {
            get { return (TimeSpan) this[PropertyNames.Max]; }
            set { this[PropertyNames.Max] = value; }
        }

        public static bool IsNullOrEmpty(TimeSpanRangeConfigurationElement element)
        {
            return element == null || element.IsEmpty();
        }

        public static TimeSpanRange? GetValue(TimeSpanRangeConfigurationElement element)
        {
            if (element == null)
            {
                return null;
            }

            return element.GetValue();
        }

        public bool HasMinValue()
        {
            return Min > default(TimeSpan);
        }

        public bool HasMaxValue()
        {
            return Max > Min;
        }

        public bool IsEmpty()
        {
            return !HasMinValue() && !HasMaxValue();
        }

        public TimeSpanRange? GetValue()
        {
            if (IsEmpty())
            {
                return null;
            }

            TimeSpanRange result = new TimeSpanRange
            {
                Min = HasMinValue() ? Min : (TimeSpan?) null,
                Max = HasMaxValue() ? Max : (TimeSpan?) null
            };

            return result;
        }

        private static class PropertyNames
        {
            public const string Min = "min";
            public const string Max = "max";
        }
    }
}