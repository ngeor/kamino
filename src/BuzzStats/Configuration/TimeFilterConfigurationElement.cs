using System.Configuration;

namespace BuzzStats.Configuration
{
    public class TimeFilterConfigurationElement : ConfigurationElement
    {
        [ConfigurationProperty("age")]
        public string Age
        {
            get { return (string) this["age"]; }
            set { this["age"] = value; }
        }

        public static bool IsNullOrEmpty(TimeFilterConfigurationElement element)
        {
            return element == null || string.IsNullOrWhiteSpace(element.Age);
        }
    }
}