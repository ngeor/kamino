// --------------------------------------------------------------------------------
// <copyright file="UpdaterConfigurationElement.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/09/04
// * Time: 1:27 μμ
// --------------------------------------------------------------------------------

using System.Configuration;

namespace BuzzStats.Configuration
{
    public class UpdaterConfigurationElement : ConfigurationElement
    {
        [ConfigurationProperty(PropertyNames.Name, IsRequired = true, IsKey = true)]
        public string Name
        {
            get { return (string) this[PropertyNames.Name]; }
            set { this[PropertyNames.Name] = value; }
        }

        [ConfigurationProperty(PropertyNames.Count, DefaultValue = 1)]
        [IntegerValidator(MinValue = 1, MaxValue = 100)]
        public int Count
        {
            get { return (int) this[PropertyNames.Count]; }
            set { this[PropertyNames.Count] = value; }
        }

        [ConfigurationProperty(PropertyNames.LastCheckedAtAge, IsRequired = false)]
        public TimeSpanRangeConfigurationElement LastCheckedAtAge
        {
            get { return (TimeSpanRangeConfigurationElement) this[PropertyNames.LastCheckedAtAge]; }
            set { this[PropertyNames.LastCheckedAtAge] = value; }
        }

        private static class PropertyNames
        {
            public const string Name = "name";
            public const string Count = "count";
            public const string LastCheckedAtAge = "lastCheckedAtAge";
        }
    }
}
