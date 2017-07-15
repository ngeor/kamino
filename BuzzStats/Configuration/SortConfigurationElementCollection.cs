using System.Configuration;

namespace BuzzStats.Configuration
{
    public class SortConfigurationElementCollection : ConfigurationElementCollection
    {
        protected override ConfigurationElement CreateNewElement()
        {
            return new SortConfigurationElement();
        }

        protected override object GetElementKey(ConfigurationElement element)
        {
            return ((SortConfigurationElement) element).Field;
        }
    }
}