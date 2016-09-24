using System.Configuration;

namespace BuzzStats.Configuration
{
    public class SortConfigurationElement : ConfigurationElement
    {
        [ConfigurationProperty("field")]
        public string Field
        {
            get { return (string) this["field"]; }
            set { this["field"] = value; }
        }

        [ConfigurationProperty("direction")]
        public string Direction
        {
            get { return (string) this["direction"]; }
            set { this["direction"] = value; }
        }
    }
}
