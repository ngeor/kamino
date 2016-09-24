using System.Configuration;

namespace BuzzStats.Configuration
{
    public class ListingPageConfigurationElement : ConfigurationElement
    {
        public ListingPageConfigurationElement()
        {
        }

        public ListingPageConfigurationElement(string name, string url)
        {
            Name = name;
            Url = url;
        }

        [ConfigurationProperty(PropertyNames.Name, IsRequired = true, IsKey = true)]
        public string Name
        {
            get { return (string) this[PropertyNames.Name]; }
            set { this[PropertyNames.Name] = value; }
        }

        [ConfigurationProperty(PropertyNames.Url, IsRequired = true)]
        public string Url
        {
            get { return (string) this[PropertyNames.Url]; }
            set { this[PropertyNames.Url] = value; }
        }

        private static class PropertyNames
        {
            public const string Name = "name";
            public const string Url = "url";
        }
    }
}
