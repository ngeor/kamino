using System;

namespace BuzzStats.Kafka
{
    public interface IConsumer
    {
        event EventHandler<string> MessageReceived;
    }
}
