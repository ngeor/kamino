// --------------------------------------------------------------------------------
// <copyright file="WebConfigurationElement.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/09/04
// * Time: 1:27 μμ
// --------------------------------------------------------------------------------

using System.Configuration;

namespace BuzzStats.Configuration
{
    public class WebConfigurationElement : ConfigurationElement
    {
        [ConfigurationProperty("disableCache", IsRequired = false)]
        public bool DisableCache
        {
            get { return (bool) this["disableCache"]; }
            set { this["disableCache"] = value; }
        }
    }
}
