// --------------------------------------------------------------------------------
// <copyright file="UpdaterConfigurationElementCollection.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/09/04
// * Time: 1:27 μμ
// --------------------------------------------------------------------------------

using System.Configuration;

namespace BuzzStats.Configuration
{
    public class UpdaterConfigurationElementCollection : ConfigurationElementCollection
    {
        public void Add(UpdaterConfigurationElement element)
        {
            BaseAdd(element);
        }

        protected override ConfigurationElement CreateNewElement()
        {
            return new UpdaterConfigurationElement();
        }

        protected override object GetElementKey(ConfigurationElement element)
        {
            return ((UpdaterConfigurationElement) element).Name;
        }
    }
}