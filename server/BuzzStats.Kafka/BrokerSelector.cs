using System;
using System.Collections.Generic;
using System.Text;

namespace BuzzStats.Kafka
{
    /// <summary>
    /// Selects the broker list.
    /// </summary>
    public static class BrokerSelector
    {
        public static string Select(string[] args)
        {
            string brokerList = null;
            if (args != null && args.Length > 0)
            {
                brokerList = args[0];
            }

            if (string.IsNullOrWhiteSpace(brokerList))
            {
                brokerList = Environment.GetEnvironmentVariable("KAFKA_BROKER");
            }

            return brokerList;
        }
    }
}
