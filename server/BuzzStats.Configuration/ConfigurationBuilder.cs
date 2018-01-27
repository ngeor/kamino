using System;

namespace BuzzStats.Configuration
{
    public static class ConfigurationBuilder
    {
        public static string KafkaBroker { get; private set; }
        public static string MongoConnectionString { get; private set; }

        public static void Build(string[] args)
        {
            KafkaBroker = Fallback(
                Environment.GetEnvironmentVariable("KAFKA_BROKER"), "192.168.99.100");
            MongoConnectionString = Fallback(
                Environment.GetEnvironmentVariable("MONGO"), "mongo://192.168.99.100:27017");
        }

        private static string Fallback(string left, string right)
        {
            return string.IsNullOrWhiteSpace(left) ? right : left;
        }
    }
}
