using System.Configuration;

namespace BuzzStats.Configuration
{
    public class ListingPagesElementCollection : ConfigurationElementCollection
    {
        protected override ConfigurationElement CreateNewElement()
        {
            return new ListingPageConfigurationElement();
        }

        protected override object GetElementKey(ConfigurationElement element)
        {
            return ((ListingPageConfigurationElement) element).Name;
        }
    }
}